package com.github.czyzby.setup.data.libs

import com.github.czyzby.setup.data.platforms.*
import com.github.czyzby.setup.data.project.Project

/**
 * Interface shared by all LibGDX extensions.
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

    fun addDesktopDependency(project: Project, dependency: String) {
        arrayOf(Desktop.ID, JGLFW.ID, LWJGL3.ID).forEach { addDependency(project, it, dependency) }
    }

    fun addNativeAndroidDependency(project: Project, dependency: String) {
        if (project.hasPlatform(Android.ID)) {
            val gradle = project.getGradleFile(Android.ID) as AndroidGradleFile
            gradle.addNativeDependency(dependency)
        }
    }

    fun addNativeMoeDependency(project: Project, dependency: String) {
        if (project.hasPlatform(MOE.ID)) {
            val gradle = project.getGradleFile(MOE.ID) as MOEGradleFile
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
