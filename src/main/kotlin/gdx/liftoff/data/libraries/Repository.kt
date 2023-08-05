package gdx.liftoff.data.libraries

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.JsonReader
import com.github.kittinunf.fuel.Fuel.get
import devcsrj.mvnrepository.MvnRepositoryApi
import gdx.liftoff.config.executeAnyOf
import gdx.liftoff.config.threadPool
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * HTTP request timeout when fetching extension versions.
 */
const val REQUEST_TIMEOUT = 30000
val json = JsonReader()

/**
 * Interface for the supported Maven repositories. Fetches the latest versions of the registered libraries.
 */
interface Repository {

  /** Returns the latest version of "[group]:[name]" artifact in this Maven repository or null if unable to fetch. */
  fun getLatestVersion(group: String, name: String): String?

  /**
   * Maven Central repository. Fetches version of libraries available through Maven Central or Sonatype OSS.
   */
  object MavenCentral : CachedRepository() {
    override fun fetchLatestVersion(group: String, name: String): String? {
      // Fetching versions from multiple services in parallel - the first result is returned:
      return executeAnyOf(
        CompletableFuture.supplyAsync({ fetchVersionFromMvnRepository(group, name) }, threadPool),
        CompletableFuture.supplyAsync({ fetchVersionFromMavenCentral(group, name) }, threadPool)
      ).get()
    }

    private fun fetchVersionFromMvnRepository(group: String, name: String): String {
      // MVNrepository is much faster than Maven Central search, but it does not report
      // beta versions and release candidates. If no version was found, the application usually
      // fallbacks to the slower Maven Central search performed in parallel.
      val versions = MvnRepositoryApi.create().getArtifactVersions(group, name)
      if (versions.isNotEmpty()) return versions.first()
      throw GdxRuntimeException("Unable to fetch $group:$name version from MVNrepository.")
    }

    private fun fetchVersionFromMavenCentral(group: String, name: String): String {
      val response = get(
        "https://search.maven.org/solrsearch/select",
        listOf(
          "q" to """g:"group"+AND+a:"$name"""",
          "rows" to "1",
          "wt" to "json"
        )
      ).timeout(REQUEST_TIMEOUT)
      val results = json.parse(response.responseString().third.get())["response"]["docs"]
      if (results.notEmpty()) {
        return results[0].getString("latestVersion")
      }
      throw GdxRuntimeException("Unable to fetch $group:$name version from Maven Central.")
    }
  }

  /**
   * JitPack Maven repository. Fetches version of libraries available through JitPack.
   */
  object JitPack : CachedRepository() {
    override fun fetchLatestVersion(group: String, name: String): String? {
      return try {
        val response = get("https://jitpack.io/api/builds/$group/$name/latest")
          .timeout(REQUEST_TIMEOUT)
        // removeSurrounding gets rid of some broken version sections resulting from JitPack -SNAPSHOT usage.
        json.parse(response.responseString().third.get()).getString("version").removeSurrounding("-", "-1")
      } catch (exception: Exception) {
        Gdx.app.error("gdx-liftoff", "Unable to perform a HTTP request to JitPack.", exception)
        null
      }
    }
  }
}

/**
 * Abstract implementation of [Repository]. Caches fetched versions.
 */
abstract class CachedRepository : Repository {
  private val versions: MutableMap<Pair<String, String>, String> = ConcurrentHashMap()

  override fun getLatestVersion(group: String, name: String): String? {
    val identifier = group to name
    versions[identifier]?.let { return@getLatestVersion it }
    // Latest version not in cache - fetching:
    val version = fetchLatestVersion(group, name)
    if (version != null) {
      // Nulls are not put in the cache. No version can be returned due to timeout errors or temporary network
      // errors. The application will attempt to fetch the version again on next call.
      versions[identifier] = version
      // It's OK if multiple threads try to fetch it and add to the map in parallel. All should have the same
      // result and the performance impact should be minimal.
    }
    return version
  }

  abstract fun fetchLatestVersion(group: String, name: String): String?
}

/**
 * Abstract implementation of [Repository] that fetches a single version once and always returns the value by
 * [Repository.getLatestVersion]. For modular libraries that share a single version.
 */
abstract class SingleVersionRepository(private val fallbackVersion: String) : Repository {
  val version by lazy { fetchLatestVersion() ?: fallbackVersion }

  override fun getLatestVersion(group: String, name: String): String = version

  abstract fun fetchLatestVersion(): String?
}
