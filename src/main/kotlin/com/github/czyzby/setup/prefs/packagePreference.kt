package com.github.czyzby.setup.prefs

import com.github.czyzby.autumn.mvc.stereotype.preference.Property

/**
 * Saves package name. Package name is usually company-dependant and often shared (at least partially) across multiple projects.
 */
@Property("Package")
class PackagePreference : AbstractStringPreference()
