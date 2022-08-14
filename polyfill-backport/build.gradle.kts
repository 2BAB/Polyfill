import me.xx2bab.polyfill.buildscript.BuildConfig.Versions

plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("me.xx2bab.polyfill.buildscript.maven-central-publish")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    implementation(gradleApi())
    implementation(deps.kotlin.std)
    compileOnly(deps.android.gradle.backport)
    compileOnly(deps.android.tools.common)
    compileOnly(deps.android.tools.sdkcommon)
    compileOnly(deps.android.tools.sdklib)
}

java {
    withSourcesJar()
    sourceCompatibility = Versions.polyfillSourceCompatibilityVersion
    targetCompatibility = Versions.polyfillTargetCompatibilityVersion
}


val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("deps")
val agpPatchIgnoredVer = versionCatalog.findVersion("agpPatchIgnoredVer").get().requiredVersion
val agpBackportPatchIgnoredVer = versionCatalog.findVersion("agpBackportPatchIgnoredVer").get().requiredVersion
buildConfig {
    buildConfigField("String", "AGP_PATCH_IGNORED_VERSION", "\"$agpPatchIgnoredVer\"")
    buildConfigField("String", "AGP_BACKPORT_PATCH_IGNORED_VERSION", "\"$agpBackportPatchIgnoredVer\"")
}


