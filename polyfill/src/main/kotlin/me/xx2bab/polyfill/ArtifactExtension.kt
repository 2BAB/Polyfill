package me.xx2bab.polyfill

import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import me.xx2bab.polyfill.artifact.ApplicationArtifactsStorage
import me.xx2bab.polyfill.artifact.LibraryArtifactsStorage

val ApplicationVariant.artifactsPolyfill: ApplicationArtifactsStorage
    get() = getExtension(ApplicationArtifactsStorage::class.java)?.also { it.prepare(this) }
        ?: throw PolyfillUninitializedException()

val LibraryVariant.artifactsPolyfill: LibraryArtifactsStorage
    get() = getExtension(LibraryArtifactsStorage::class.java)?.also { it.prepare(this) }
        ?: throw PolyfillUninitializedException()

class PolyfillUninitializedException : Exception(
    "Polyfill is not yet initialized," +
            " please apply Polyfill plugin before calling any APIs following by the instruction."
)