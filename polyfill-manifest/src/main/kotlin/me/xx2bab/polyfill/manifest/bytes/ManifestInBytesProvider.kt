package me.xx2bab.polyfill.manifest.bytes

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import me.xx2bab.polyfill.matrix.base.ApplicationSelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.RegularFile

class ManifestInBytesProvider: ApplicationSelfManageableProvider<RegularFile> {

    override fun initialize(project: Project,
                            androidExtension: AndroidComponentsExtension<*, *, *>,
                            variant: Variant) {

    }


    override fun get(defaultValue: RegularFile?): RegularFile? {
        return null
    }

    override fun isPresent(): Boolean {
        return false
    }

}