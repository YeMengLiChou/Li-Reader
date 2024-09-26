package cn.li.reader.convention.common

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

val Project.libs
    get(): VersionCatalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")


fun VersionCatalog.getPluginId(name: String): String = findPlugin(name).get().get().pluginId