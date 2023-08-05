package gdx.liftoff.data.libraries

private val camelCase = Regex("(.)(\\p{Upper})")
fun String.camelCaseToKebabCase(): String = replace(camelCase, "$1-$2").lowercase()
