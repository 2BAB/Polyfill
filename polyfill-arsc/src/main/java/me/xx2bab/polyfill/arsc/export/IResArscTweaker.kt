package me.xx2bab.polyfill.arsc.export

import java.io.File
import java.io.IOException

/**
 * The export api for resource.arsc tool.
 */
interface IResArscTweaker {

    /**
     * To load the arsc file into tweaker.
     * @param
     */
    @Throws(IOException::class)
    fun read(source: File)

    /**
     * To write a new arsc file to specify file.
     * @param
     */
    @Throws(IOException::class)
    fun write(dest: File)

    /**
     * @return Return types with <name, id>
     */
    fun getResourceTypes(): Map<String, Int>

    /**
     * @param id resource ID
     * @return SimpleResource instance or null
     */
    fun findResourceById(id: Int): List<SimpleResource?>

    /**
     * @param id resource ID
     * @return SimpleResource instance or null
     */
    fun removeResourceById(id: Int): Boolean

    fun updateResourceById(resource: SimpleResource,
                           config: SupportedResConfig): Boolean

}