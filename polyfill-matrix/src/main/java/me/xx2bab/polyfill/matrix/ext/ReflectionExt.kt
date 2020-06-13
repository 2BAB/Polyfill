package me.xx2bab.polyfill.matrix.ext

fun <T> getField(clazz: Class<T>, instance: T, fieldName: String): Any {
    val field = clazz.declaredFields.filter { it.name == fieldName }[0]
    field.isAccessible = true
    return field.get(instance) as Any
}