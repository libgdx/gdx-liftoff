package com.github.czyzby.setup.data.gradle

import com.badlogic.gdx.files.FileHandle
import com.github.czyzby.setup.data.files.ProjectFile
import java.io.File

abstract class GradleFile private constructor(override val path: String) : ProjectFile {
    val buildDependencies = mutableSetOf<String>()
    val dependencies = mutableSetOf<String>()

    constructor(projectName: String, fileName: String = "build.gradle") : this(if (projectName.isNotEmpty()) {
        projectName + File.separator
    } else {
        ""
    } + fileName)

    fun joinDependencies(dependencies: Collection<String>, type: String = "api", tab: String = "\t"): String = if (dependencies.isEmpty()) "\n" else
        dependencies.joinToString(prefix = "$tab$type ", separator = "\n$tab$type ", postfix = "\n")

    /**
     * @param dependency will be added as "compile" dependency, quoted.
     */
    fun addDependency(dependency: String) = dependencies.add("\"$dependency\"")

    override fun save(destination: FileHandle) {
        destination.child(path).writeString(getContent(), false, "UTF-8")
    }

    abstract fun getContent(): String
}