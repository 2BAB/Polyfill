package me.xx2bab.polyfill

import com.android.Version
import me._bab.polyfill_backport.BuildConfig.AGP_BACKPORT_PATCH_IGNORED_VERSION
import me._bab.polyfill_backport.BuildConfig.AGP_PATCH_IGNORED_VERSION
import me.xx2bab.polyfill.tools.SemanticVersionLite

/**
 * This is not a reusable design, we create it to solve the AGP compatible issues solely.
 * The target is to provide quick and smooth upgrade experience of code base whenever AGP moves on,
 * so that Polyfill can match the latest AGP internal changes.
 */
abstract class BackportPatch<Result> {

    /**
     * Depending on the AGP version that current project uses, the function decides to
     *   - apply the patch for backport AGP (e.g. 7.1).
     *   - or execute a given default action for latest stable AGP (e.g. 7.2).
     */
    fun applyOrDefault(action: () -> Result): Result {
        val targetVer = SemanticVersionLite(AGP_PATCH_IGNORED_VERSION)
        val backportVer = SemanticVersionLite(AGP_BACKPORT_PATCH_IGNORED_VERSION)
        val currVer = SemanticVersionLite(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        return if (backportVer >= currVer && backportVer < targetVer) {
            apply()
        } else { // backportVer > targetVer
            action.invoke()
        }
    }

    abstract fun apply(): Result

}