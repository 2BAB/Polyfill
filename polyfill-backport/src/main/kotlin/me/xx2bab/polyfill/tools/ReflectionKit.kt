package me.xx2bab.polyfill.tools

object ReflectionKit {

    fun <T> getField(clazz: Class<T>, instance: T, fieldName: String): Any {
        val field = clazz.declaredFields.first { it.name == fieldName }
        field.isAccessible = true
        return field.get(instance) as Any
    }

}