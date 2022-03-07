package me.xx2bab.polyfill

import me.xx2bab.polyfill.manifest.PreHookManifestConfigureAction
import me.xx2bab.polyfill.task.MultipleArtifactPincerTaskConfiguration
import me.xx2bab.polyfill.task.PincerTaskConfiguration
import me.xx2bab.polyfill.task.SingleArtifactPincerTaskConfiguration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

abstract class PolyfillExtension {

    internal val locked = AtomicBoolean(false)

    internal val singleArtifactMap = mutableMapOf<PolyfilledArtifact<*>,
            KClass<out PincerTaskConfiguration<*, *, *>>>(

    )

    internal val multipleArtifactMap = mutableMapOf<PolyfilledArtifact<*>,
            KClass<out PincerTaskConfiguration<*, *, *>>>(
        ManifestCollection to PreHookManifestConfigureAction::class
    )

    fun registerPincerTaskConfig(
        artifactType: PolyfilledSingleArtifact<*, *>,
        kClass: KClass<out SingleArtifactPincerTaskConfiguration<*, *>>
    ) {
        if (locked.get()) {
            return
        }
        singleArtifactMap[artifactType] = kClass
    }

    fun registerPincerTaskConfig(
        artifactType: PolyfilledMultipleArtifact<*, *>,
        kClass: KClass<out MultipleArtifactPincerTaskConfiguration<*, *>>
    ) {
        if (locked.get()) {
            return
        }
        multipleArtifactMap[artifactType] = kClass
    }

}