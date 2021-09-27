package com.github.czyzby.setup.data.libs

import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project

/**
 * Interface shared by all libGDX extensions.
 * @author MJ
 */
interface Library {
    val id: String
    val defaultVersion: String
    val url: String
    val official: Boolean

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
