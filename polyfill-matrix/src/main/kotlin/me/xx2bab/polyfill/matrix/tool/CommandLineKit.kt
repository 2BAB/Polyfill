package me.xx2bab.polyfill.matrix.tool

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object CommandLineKit {

    private var workingDir = File("./")

    fun runCommand(
        command: String,
        workingDir: File = CommandLineKit.workingDir,
        timeoutInMilliseconds: Long = 10 * 1000
    ): String? {
        return try {
            val parts = command.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
            proc.waitFor(timeoutInMilliseconds, TimeUnit.MILLISECONDS)
            proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}