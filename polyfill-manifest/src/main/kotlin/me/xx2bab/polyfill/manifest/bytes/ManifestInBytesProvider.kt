package me.xx2bab.polyfill.manifest.bytes

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import me.xx2bab.polyfill.matrix.base.SelfManageableProvider
import org.gradle.api.Project
import org.gradle.api.file.RegularFile

class ManifestInBytesProvider: SelfManageableProvider<RegularFile> {

    override fun initialize(project: Project,
                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                            variant: VariantProperties) {

    }


    override fun get(defaultValue: RegularFile?): RegularFile? {
        return null
    }

    override fun isPresent(): Boolean {
        return false
    }

}