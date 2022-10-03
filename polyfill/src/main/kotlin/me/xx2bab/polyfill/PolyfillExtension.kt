package me.xx2bab.polyfill

import me.xx2bab.polyfill.jar.JavaResourceMergePreHookConfiguration
import me.xx2bab.polyfill.manifest.ManifestMergePreHookConfiguration
import me.xx2bab.polyfill.res.ResourceMergePostHookConfiguration
import me.xx2bab.polyfill.res.ResourceMergePreHookConfiguration
import me.xx2bab.polyfill.task.MultipleArtifactTaskExtendConfiguration
import me.xx2bab.polyfill.task.SingleArtifactTaskExtendConfiguration
import me.xx2bab.polyfill.task.TaskExtendConfiguration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

abstract class PolyfillExtension {

    internal val locked = AtomicBoolean(false)

    internal val singleArtifactMap = mutableMapOf<PolyfilledArtifact<*>,
            KClass<out TaskExtendConfiguration<*>>>(
        PolyfilledSingleArtifact.MERGED_RESOURCES to ResourceMergePostHookConfiguration::class
    )

    internal val multipleArtifactMap = mutableMapOf<PolyfilledArtifact<*>,
            KClass<out TaskExtendConfiguration<*>>>(
        PolyfilledMultipleArtifact.ALL_MANIFESTS to ManifestMergePreHookConfiguration::class,
        PolyfilledMultipleArtifact.ALL_RESOURCES to ResourceMergePreHookConfiguration::class,
        PolyfilledMultipleArtifact.ALL_JAVA_RES to JavaResourceMergePreHookConfiguration::class
    )

    /**
     * To register a custom [SingleArtifactTaskExtendConfiguration] for [PolyfilledSingleArtifact].
     */
    fun registerTaskExtensionConfig(
        artifactType: PolyfilledSingleArtifact<*, *>,
        kClass: KClass<out SingleArtifactTaskExtendConfiguration<*>>
    ) {
        if (locked.get()) {
            return
        }
        singleArtifactMap[artifactType] = kClass
    }

    /**
     * To register a custom [MultipleArtifactTaskExtendConfiguration] for [PolyfilledMultipleArtifact].
     */
    fun registerTaskExtensionConfig(
        artifactType: PolyfilledMultipleArtifact<*, *>,
        kClass: KClass<out MultipleArtifactTaskExtendConfiguration<*>>
    ) {
        if (locked.get()) {
            return
        }
        multipleArtifactMap[artifactType] = kClass
    }

}