<img src="./Polyfill.png" alt="Polyfill" width="507px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/polyfill/badge.svg)](https://search.maven.org/artifact/me.2bab/polyfill)
[![Actions Status](https://github.com/2bab/Polyfill/workflows/CI/badge.svg)](https://github.com/2bab/Polyfill/actions)
[![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[English] [[中文]](./README_zh.md)

🚧 **It's currently under incubating...**

Polyfill is an artifact repository to assist writing Gradle Plugins for Android build system. It provides addtional artifacts in similar API styles of AGP Artifacts ones for third party plugin developers.

If you are not familiar with new Artifact/Variant API of AGP (since 7.0), please check the tutorial [Gradle and AGP build APIs - MAD Skills](https://www.youtube.com/playlist?list=PLWz5rJ2EKKc8fyNmwKXYvA2CqxMhXqKXX) by @AndroidDevelopers. More information can be found on "Why Polyfill" section below.


## Quick Start

1. Add Polyfill to dependencies of your Plugin project (standalone plugin project or `buildSrc`):

``` kotlin
dependencies {
    compileOnly("com.android.tools.build:gradle:8.1.2")
    implementation("me.2bab:polyfill:0.9.0")  <--
}
```

2. Apply the Polyfill plugin to your plugin before everything:

``` Kotlin
import org.gradle.kotlin.dsl.apply

class TestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.apply(plugin = "me.2bab.polyfill")  <--
        ...
    }
}    
```

3. Config your `TaskProvider` (for `get/getAll()`) or `PolyfillAction`(for `use()` as well as `get/getAll()`) with the help of Polyfill's `variant.artifactsPolyfill.*` AIs, which has similar style with `variant.artifacts` ones of AGP:

``` kotlin
val androidExtension = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
androidExtension.onVariants { variant ->

    // get()/getAll()
    val printManifestTask = project.tasks.register<PreUpdateManifestsTask>(
        "getAllInputManifestsFor${variant.name.capitalize()}"
    ) {
        beforeMergeInputs.set(
            variant.artifactsPolyfill.getAll(PolyfilledMultipleArtifact.ALL_MANIFESTS)  <--
        )
    }
    ...

    // use()
    val preHookManifestTaskAction1 = PreUpdateManifestsTaskAction(buildDir, id = "preHookManifestTaskAction1")
    variant.artifactsPolyfill.use(
        action = preHookManifestTaskAction1,
        toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
    )
}

... 
class PreUpdateManifestsTaskAction(
    buildDir: File,
    id: String
) : PolyfillAction<List<RegularFile>> {

    override fun onTaskConfigure(task: Task) {}

    override fun onExecute(artifact: Provider<List<RegularFile>>) {
        artifact.get().let { files ->
            files.forEach {
                val manifestFile = it.asFile
                // Check per manifest input and filter whatever you want, remove broken pieces, etc.
                // val updatedContent = manifestFile.readText().replace("abc", "def")
                // manifestFile.writeText(updatedContent)
            }
        }
    }
    
}
```

All supported Artifacts are listed below: 

|PolyfilledSingleArtifact|Data Type|Description|
|:---:|:---:|:---:|
|MERGED_RESOURCES|`Provider<Directory>`|To retrieve merged `/res` directory.|


| PolyfilledMultipleArtifact |Data Type|                                       Description                                       |
|:--------------------------:|:---:|:---------------------------------------------------------------------------------------:|
|       ALL_MANIFESTS        |`ListProvider<RegularFile>`| To retrieve all `AndroidManifest.xml` regular files that will paticipate merge process. |
|       ALL_RESOURCES        |`ListProvider<Directory>`|         To retrieve all `/res` directories that will paticipate merge process.          |
|        ALL_JAVA_RES        |`ListProvider<RegularFile>`|               To retrieve all Java Resources that will paticipate merge process.               |

In addition, `Artifact.Single<FILE_TYPE>`，`Artifact.Multiple<FILE_TYPE>` and their implementations such as `InternalArtifactType` are  supported by `get(...)/getAll(...)`. You can access more internal Artifacts of AGP through them.

4. If aforementioned API sets are not satisfied for your requirement, a public data pipeline mechanism with a bunch of variant tools that provided by Polyfill are opening to customized Artifacts registry.（PR is welcome as well!)

``` Kotlin
project.extensions.getByType<PolyfillExtension>()
    .registerTaskExtensionConfig(DUMMY_SINGLE_ARTIFACT, DummySingleArtifactImpl::class)
```

Check more examples in `./polyfill-test-plugin` and `./polyfill/src/functionalTest`.


## Why Polyfill?

As its name suggests, the lib is a **middle-ware** between **AGP** (Android Gradle Plugin) and **3rd Gradle Plugin** based on AGP context. For example, the [ScratchPaper](https://github.com/2BAB/ScratchPaper) is a plugin to add an overlay to your app icons which based on AGP, it consumes:

1. SDK Locations / BuildToolInfo instance (to run aapt2 commands)
2. All input resource directories (to query the source of launcher icons)
3. Merged AndroidManifest.xml (to get the resolved icon name)

By the time I created ScratchPaper, AGP does not provide any public API for above 3 items, I had to deal them with a few hacky hooks. In 2018, I started to consider if we can make a Polyfill layer for 3rd Android Gradle Plugin developers, and finally released the first version in 2020 as you can see here. The name "Polyfill" comes from the FrontEnd tech-stack, which makes the JS code compatible with old/odd browser APIs.

Since AGP 7.0.0, the AGP team provides a new public API set called **"Variant/Artifact API"**. You can check all latest AGP exported Artifacts here: [SingleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/SingleArtifact), [MultipleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifact) (On "Known Direct Subclasses" section). At this early stage AGP only provides less than 10 artifacts' API, **AGP released 2-3 minor versions per year, developers need to stay tuned for new Artifacts releasing.** Back to the example, only item 3 is provided by the new Artifacts API in public. For rest two items you may need to handle(hack) by yourself. to fulfill thoes requirements that are not satisfied by new Artifacts so far, probably we can:

1. Raise requests to corresponding issue tracker thread of [AGP](https://issuetracker.google.com/issues?q=componentid:192709). 
2. In the meantime, create a similar data pipeline to populate our hooks as what `artifacts.use()/get()/getAll()` looks like, it's a temporary workaround and easy to migrate to official Artifacts API once available.

That's the reason why I created Polyfill and wish one day we can 100% migrate to Artifacts API. Find more Variant/Artifact API news from links below:

- [gradle-recipes](https://github.com/android/gradle-recipes): An official repo for Variant/Artifact API showcases.
- [New APIs in the Android Gradle Plugin](https://medium.com/androiddevelopers/new-apis-in-the-android-gradle-plugin-f5325742e614): A brief orientation for new Variant/Artifact API.
- [Extend the Android Gradle plugin](https://developer.android.com/studio/build/extend-agp): The official doc of Variant/Artifact API that was released in Oct 2021.


## Compatible Specification

Polyfill is only supported & tested on latest **2** Minor versions of Android Gradle Plugin.

**Changelog** can be found from [Github Releases](https://github.com/2BAB/Polyfill/releases).

|  AGP Version  |      Latest Support Version      |
|:-------------:|:--------------------------------:|
| 8.1.x / 8.0.x |              0.9.0               |
| 7.2.x / 7.1.x |              0.8.0               |
| 7.2.x / 7.1.x |              0.7.0               |
|     7.1.x     |              0.6.2               |
|     7.0.x     |              0.4.1               |
|     4.2.0     | 0.3.1 (Migrated to MavenCentral) |


## Git Commit Check

Check this [link](https://medium.com/walmartlabs/check-out-these-5-git-tips-before-your-next-commit-c1c7a5ae34d1) to
make sure everyone will make a **meaningful** commit message.

So far we haven't added any hook tool, but follow the regex below:

```
(chore|feat|docs|fix|refactor|style|test|hack|release)(:)( )(.{0,80})
```


## License

>
> Copyright Since 2018 2BAB
>
> Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
>
>   http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
