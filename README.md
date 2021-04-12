<img src="./Polyfill.png" alt="Polyfill" width="507px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/polyfill/badge.svg)](https://search.maven.org/artifact/me.2bab/polyfill)
 [![Actions Status](https://github.com/2bab/Polyfill/workflows/CI/badge.svg)](https://github.com/2bab/Polyfill/actions) [![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)


[English] [[中文]](./README_zh.md)

🚧 **It's currently under incubating...**

Polyfill is a hook toolset for Android App Build System.

## Why need Polyfill?

As its name suggests, the lib is a **middle-ware** between **AGP** (Android Gradle Plugin) and **3rd Gradle Plugin** based on AGP context. For example, the [ScratchPaper](https://github.com/2BAB/ScratchPaper) is a plugin to add an overlay to your app icons which based on AGP, it consumes: 

- SDK Locations / BuildToolInfo instance (to run aapt commands) 
- Merged AndroidManifest.xml (to get the resolved icon name)

Those inputs usually caused problems:

1. They are open-source but not exposed directly, sometimes you may need to use reflect to retrieve the input you need.
2. They may change when updating to a new AGP version, again, because those are mainly for internal usage.

In 2018, I started to consider if we can make a Polyfill layer for 3rd Android Gradle Plugin developers, and finally released the first version in 2020 as you can see here. The name "Polyfill" comes from the FrontEnd tech stack, which makes the JS code compatible with old/odd browser APIs.

To be noticed, starting from AGP 4.1.0, the AGP team provides a new public API set **"Artifacts"**. However, **this is a very early stage that only provides less than 10 artifacts' API for developers to use, and since AGP released 2-3 minor versions per year, developers may not expect their own problems got quickly fixed in recent 2-3 years.**

That's why I still insist to create Polyfill lib and wish one day we can 100% migrate to Artifacts API.

Find more Artifaces API news from links below:

- [gradle-recipes](https://github.com/android/gradle-recipes): which is the official Artifacts API showcases.
- [New APIs in the Android Gradle Plugin](https://medium.com/androiddevelopers/new-apis-in-the-android-gradle-plugin-f5325742e614) : a brief orientation for new Artifacts API.

## What does Polyfill provide?

- It encapsulates AGP (Android Gradle Plugin) APIs, turn them to **Task Hook Points** (Listener) and **Task Inputs** for all 3rd plugin developer to easily interact with.
    - **Task Hook Points:** for instance, if the developer wants to intercept manifest merge input files, he/she should find producer task(s) and consumer task of input files, then add a new custom task that will be executed between them; here the Hook Points means we define a Listener who process the task order stuffs and make sure the new-added runs on the true timing, Polyfill provides many `AGPTaskListener.kt` (the impl such as `ManifestBeforeMergeListener.kt`) to complete this job.
    - **Task Inputs:** with the help of Hook Points, your task logic can now be executed during the right time, what you might miss is task input(s), for example, Manifest files, Android SDK locations, AGP versions, etc. To configure them easily, Polyfill provides `SelfManageableProvider.kt` (the impl such as `ManifestMergeInputProvider.kt`) to fetch the task input(s).
- Meanwhile, it provides a bunch of tools working on build intermediates, such as binary parser and builder for `resources.arsc`, `AndroidManifest.xml`.

## Quick Start

1. Add Polyfill to build classpath:

``` kotlin
// Root project's build.gradle.kts
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-rc01")
        classpath("me.2bab:polyfill:0.2.0")
    }
}

// If add to /buildSrc/build.gradle.kts or standalone plugin project, switch to implementation instead
// dependencies {
//     implementation("me.2bab:polyfill:0.2.0")
// }
```

2. Write some custom tasks based on Polyfill (follow the steps of comment):

``` kotlin
// 0. Create Polyfill instance (per project):

// Create for application module
val polyfill = Polyfill.createApplicationPolyfill(project)
// Create for library module
// Polyfill.createLibraryPolyfill(project)

// 1. Start onVariantProperties
polyfill.onVariantProperties {
    val variant = this
    // 3. Create & Config the hook task.
    val preUpdateTask = project.tasks.register("preUpdate${variant.name.capitalize()}Manifest",
            ManifestBeforeMergeTask::class.java) {
        beforeMergeInputs.set(polyfill.getProvider(variant, ManifestMergeInputProvider::class.java).get())
    }
    // 4. Add it with the listener (which plays the role of entry for a hook).
    val beforeMergeListener = ManifestBeforeMergeListener(preUpdateTask)
    polyfill.addAGPTaskListener(variant, beforeMergeListener)


    // Let's try again with after merge hook
    val postUpdateTask = project.tasks.register("postUpdate${variant.name.capitalize()}Manifest",
            ManifestAfterMergeTask::class.java) {
        afterMergeInputs.set(polyfill.getProvider(variant, ManifestMergeOutputProvider::class.java).get())
    }
    val afterMergeListener = ManifestAfterMergeListener(postUpdateTask)
    polyfill.addAGPTaskListener(variant, afterMergeListener)
}

// 2. Prepare the task containing specific hook logic.
abstract class ManifestBeforeMergeTask : DefaultTask() {
    @get:InputFiles
    abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

    @TaskAction
    fun beforeMerge() {
        val manifestPathsOutput = FunctionTestFixtures.getOutputFile(project, "manifest-merge-input.json")
        manifestPathsOutput.createNewFile()
        beforeMergeInputs.get().let { set ->
            manifestPathsOutput.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
        }
    }
}

abstract class ManifestAfterMergeTask : DefaultTask() {

    @get:InputFiles
    abstract val afterMergeInputs: RegularFileProperty

    @TaskAction
    fun afterMerge() {
        if (afterMergeInputs.isPresent) {
            val file = afterMergeInputs.get().asFile
            val modifiedManifest = file.readText()
                    .replace("allowBackup=\"true\"", "allowBackup=\"false\"")
            file.writeText(modifiedManifest)
        }
    }

}
```

Check more in `./test-project` and `./polyfill/src/funcTest`.

## Compatible Specification

Polyfill is only supported & tested on latest **2** Minor versions of Android Gradle Plugin.

**Changelog** can be found from [Github Releases](https://github.com/2BAB/Polyfill/releases).

AGP Version| Latest Support Version
:-----------:|:-----------------:
4.2.0-rc01 | 0.2.0 (MavenCentral)
4.2.0-alpha15 | 0.1.3 (JCenter)

(The project currently compiles with the latest version of AGP 4.2, and compiles and tests against the both AGP 4.2 and 7.0 on CI.)

## Git Commit Check

Check this [link](https://medium.com/walmartlabs/check-out-these-5-git-tips-before-your-next-commit-c1c7a5ae34d1) to make sure everyone will make a **meaningful** commit message.

So far we haven't added any hook tool, but follow the regex below:

```
(chore|feat|docs|fix|refactor|style|test|hack|release)(:)( )(.{0,80})
```


## License

>
> Copyright 2018-2021 2BAB
>
>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
>
>   http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
