package me.xx2bab.polyfill.arsc.export

data class SimpleResource(
        val id: Int,
        val type: SupportedResType,
        val name: String?,
        val value: String?) {

    companion object {

    }

}