package me.xx2bab.polyfill

import me.xx2bab.polyfill.manifest.ManifestMergePreHookConfigureAction
import me.xx2bab.polyfill.res.ResourceMergePostHookConfigureAction
import me.xx2bab.polyfill.res.ResourceMergePreHookConfigureAction
import me.xx2bab.polyfill.task.MultipleArtifactPincerTaskConfiguration
import me.xx2bab.polyfill.task.PincerTaskConfiguration
import me.xx2bab.polyfill.task.SingleArtifactPincerTaskConfiguration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

abstract class PolyfillExtension {

    internal val locked = AtomicBoolean(false)

    internal val singleArtifactMap = mutableMapOf<PolyfilledArtifact<*>,
            KClass<out PincerTaskConfiguration<*>>>(
        PolyfilledSingleArtifact.MERGED_RESOURCES to ResourceMergePostHookConfigureAction::class
    )

    internal val multipleArtifactMap = mutableMapOf<PolyfilledArtifact<*>,
            KClass<out PincerTaskConfiguration<*>>>(
        PolyfilledMultipleArtifact.ALL_MANIFESTS to ManifestMergePreHookConfigureAction::class,
        PolyfilledMultipleArtifact.ALL_RESOURCES to ResourceMergePreHookConfigureAction::class
    )

    fun registerPincerTaskConfig(
        artifactType: PolyfilledSingleArtifact<*, *>,
        kClass: KClass<out SingleArtifactPincerTaskConfiguration<*>>
    ) {
        if (locked.get()) {
            return
        }
        singleArtifactMap[artifactType] = kClass
    }

    fun registerPincerTaskConfig(
        artifactType: PolyfilledMultipleArtifact<*, *>,
        kClass: KClass<out MultipleArtifactPincerTaskConfiguration<*>>
    ) {
        if (locked.get()) {
            return
        }
        multipleArtifactMap[artifactType] = kClass
    }

}