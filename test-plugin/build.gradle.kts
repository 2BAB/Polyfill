plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}

dependencies {
//    implementation(fileTree(mapOf("dir" to "../../build/libs", "include" to arrayOf("*.jar"))))
//    implementation("me.2bab:polyfill:+")
    implementation(project(":polyfill"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.31")
    implementation("com.android.tools.build:gradle:4.2.0-beta06")
    implementation("com.alibaba:fastjson:1.2.73")
}

gradlePlugin {
    plugins.register("polyfill-test-plugin") {
        id = "polyfill-test-plugin"
        implementationClass = "me.xx2bab.polyfill.test.TestPlugin"
    }
}