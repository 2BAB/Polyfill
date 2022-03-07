//package me.xx2bab.polyfill.manifest
//
//import com.android.build.api.variant.AndroidComponentsExtension
//import com.android.build.api.variant.Variant
//import com.android.build.gradle.internal.publishing.AndroidArtifacts
//import me.xx2bab.polyfill.agp.toApkCreationConfigImpl
//import me.xx2bab.polyfill.creationdata.ApplicationCreationDataSnack
//import org.gradle.api.Project
//import org.gradle.api.file.FileSystemLocation
//import org.gradle.api.provider.Provider
//
//class ManifestMergeInputProvider : ApplicationCreationDataSnack<Provider<Set<FileSystemLocation>>> {
//
//    private lateinit var manifests: Provider<Set<FileSystemLocation>>
//
//
//
//    override fun obtain(defaultValue: Provider<Set<FileSystemLocation>>?): Provider<Set<FileSystemLocation>> {
//        return manifests
//    }
//
//}