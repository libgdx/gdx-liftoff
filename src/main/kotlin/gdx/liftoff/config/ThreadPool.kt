package gdx.liftoff.config

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * Main thread pool for asynchronous operations. Automatically resizes on demand.
 */
val threadPool: ExecutorService = Executors.newCachedThreadPool(PrefixedThreadFactory("gdx-liftoff"))
// Implementation note: cached thread pool was chosen over ForkJoinPool as we are performing multiple I/O
// operations in parallel. This would quickly flood the default ForkJoinPool, with threads stuck on data fetching.

/**
 * Generates sane thread names for [Executors].
 */
private class PrefixedThreadFactory(threadPrefix: String) : ThreadFactory {
  private val count = AtomicLong(0)
  private val threadPrefix: String

  init {
    this.threadPrefix = "$threadPrefix-"
  }

  override fun newThread(runnable: Runnable): Thread {
    val thread = Executors.defaultThreadFactory().newThread(runnable)
    thread.name = threadPrefix + count.andIncrement
    return thread
  }
}

/**
 * Utility function that allows to execute many [tasks] in parallel. Returns a [CompletableFuture] completed with
 * the result of the one that finished first, or null of all the [tasks] failed.
 *
 * In contrary to the [CompletableFuture.anyOf], this function ignores exceptions and waits for the first task that
 * completes successfully. This is useful for executing operations that rely on third-party services.
 */
fun <T> executeAnyOf(vararg tasks: CompletableFuture<out T>): CompletableFuture<T?> {
  val result = CompletableFuture<T?>()
  tasks.forEach { task -> task.thenAccept { result.complete(it) } }
  val taskAggregator = CompletableFuture.allOf(*tasks).exceptionally {
    // None of the tasks managed to execute successfully - completing the future with null:
    result.complete(null)
    null
  }
  // Cancelling other tasks:
  result.thenAccept {
    tasks.forEach { it.cancel(true) }
    taskAggregator.cancel(true)
  }
  return result
}
