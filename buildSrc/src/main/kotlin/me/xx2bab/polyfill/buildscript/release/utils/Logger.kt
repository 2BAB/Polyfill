package me.xx2bab.polyfill.buildscript.release.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object Logger {

    private const val TAG = "[buildSrc]: "

    private lateinit var logUtil: Logger

    fun init(project: Project) {
        logUtil = project.logger
    }

    fun d(message: String) {
        logUtil.debug(TAG + message)
    }

    fun i(message: String) {
        logUtil.info(TAG + message)
    }

    fun e(message: String) {
        logUtil.error(TAG + message)
    }


}