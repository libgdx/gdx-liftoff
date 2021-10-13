package com.github.czyzby.setup.data.libs

import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project

/**
 * Interface shared by all libGDX extensions.
 */
interface Library {
    val id: String
    val defaultVersion: String
    val url: String
    val official: Boolean
    val repository: Repository
    /** Group of the main dependency used to determine the version. */
    val group: String
    /** Name of the main dependency used to determine the version. */
    val name: String

    /**
     * @param project is currently generated and should have this library included.
     */
    fun initiate(project: Project)

    fun addDependency(project: Project, platform: String, dependency: String) {
        if (project.hasPlatform(platform)) {
            project.getGradleFile(platform).addDependency(dependency)
        }
    }

    fun addSpecialDependency(project: Project, platform: String, dependency: String) {
        if (project.hasPlatform(platform)) {
            project.getGradleFile(platform).addSpecialDependency(dependency)
        }
    }

    fun addDesktopDependency(project: Project, dependency: String) {
        addDependency(project, Desktop.ID, dependency)
        addDependency(project, LWJGL3.ID, dependency)
    }

    fun addNativeAndroidDependency(project: Project, dependency: String) {
        if (project.hasPlatform(Android.ID)) {
            val gradle = project.getGradleFile(Android.ID) as AndroidGradleFile
            gradle.addNativeDependency(dependency)
        }
    }

    fun addGwtInherit(project: Project, inherit: String) {
        if (project.hasPlatform(GWT.ID)) {
            project.gwtInherits.add(inherit)
        }
    }

    fun addAndroidPermission(project: Project, permissionName: String) {
        if (project.hasPlatform(Android.ID)) {
            project.androidPermissions.add(permissionName)
        }
    }
}

enum class Repository {
    MAVEN_CENTRAL, JITPACK, MAVEN_SNAPSHOTS
}

private val camelCase = Regex("(.)(\\p{Upper})")
fun String.camelCaseToKebabCase(): String = replace(camelCase, "$1-$2").lowercase()
