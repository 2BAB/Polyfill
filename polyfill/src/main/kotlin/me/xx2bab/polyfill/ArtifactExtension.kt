package me.xx2bab.polyfill

import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import me.xx2bab.polyfill.artifact.ApplicationArtifactsRepository
import me.xx2bab.polyfill.artifact.LibraryArtifactsRepository

/**
 * Main entry of the Polyfill library, to provide similar function of
 * [ApplicationVariant.artifacts].
 *
 * @return [ApplicationArtifactsRepository]
 */
val ApplicationVariant.artifactsPolyfill: ApplicationArtifactsRepository
    get() = getExtension(ApplicationArtifactsRepository::class.java)
        ?: throw PolyfillUninitializedException()

/**
 * Main entry of the Polyfill library, to provide similar function of
 * [LibraryVariant.artifacts].
 *
 * @return [ApplicationArtifactsRepository]
 */
val LibraryVariant.artifactsPolyfill: LibraryArtifactsRepository
    get() = getExtension(LibraryArtifactsRepository::class.java)
        ?: throw PolyfillUninitializedException()


class PolyfillUninitializedException : Exception(
    "Polyfill is not yet initialized," +
            " please apply Polyfill plugin before calling any APIs following by the instruction."
)