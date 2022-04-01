<img src="./Polyfill.png" alt="Polyfill" width="507px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/polyfill/badge.svg)](https://search.maven.org/artifact/me.2bab/polyfill)
[![Actions Status](https://github.com/2bab/Polyfill/workflows/CI/badge.svg)](https://github.com/2bab/Polyfill/actions)
[![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[[English]](./README.md) [中文]

🚧 **孵化中...**

Polyfill 是一个第三方的**工件仓库**，服务于编写 Android 构建环境下的 Gradle 插件，提供了与 Android Gradle Plugin(AGP) 的 Artifacts API 风格类似的接口给第三方插件开发者。

如果你不熟悉 AGP 的新 Artifact/Variant，请查看这份 @AndroidDevelopers 的官方指南 [Gradle and AGP build APIs - MAD Skills](https://www.youtube.com/playlist?list=PLWz5rJ2EKKc8fyNmwKXYvA2CqxMhXqKXX)。更多信息请参考下方“为什么需要 Polyfill”小节。


## 快速上手

1. 添加 Polyfill 至你的插件工程（独立的插件工程或者 `buildSrc`）：

``` kotlin
dependencies {
    compileOnly("com.android.tools.build:gradle:7.1.2")
    implementation("me.2bab:polyfill:0.6.2")  <--
}
```

2. 应用 Polyfill 插件至你的插件 `apply(...)` 方法（最好在一切开始之前）：


``` Kotlin
import org.gradle.kotlin.dsl.apply

class TestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.apply(plugin = "me.2bab.polyfill")  <--
        ...
    }
}    
```

3. 借助 Polyfill 的 `variant.artifactsPolyfill.*` 配置你的 `TaskProvider`，其 API 风格与 AGP 的 `variant.artifacts` 相近：

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
    val preHookManifestTask1 = project.tasks.register<PreUpdateManifestsTask>(
        "preUpdate${variant.name.capitalize()}Manifest1"
    )
    variant.artifactsPolyfill.use(  <--
        taskProvider = preHookManifestTask1,
        wiredWith = TestPlugin.PreUpdateManifestsTask::beforeMergeInputs,
        toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
    )
}

...

abstract class PreUpdateManifestsTask : DefaultTask() {
    @get:InputFiles
    abstract val beforeMergeInputs: ListProperty<RegularFile>  <--

    @TaskAction
    fun beforeMerge() {
        beforeMergeInputs.get().let { files -> ...}
    }
}
```

所有 Polyfill 支持的工件已在下方列出：


|PolyfilledSingleArtifact|Data Type|Description|
|:---:|:---:|:---:|
|MERGED_RESOURCES|`Provider<Directory>`|To retrieve merged `/res` directory.|


|PolyfilledMultipleArtifact|Data Type|Description|
|:---:|:---:|:---:|
|       ALL_MANIFESTS        |`ListProvider<RegularFile>`| To retrieve all `AndroidManifest.xml` regular files that will paticipate merge process. |
|       ALL_RESOURCES        |`ListProvider<Directory>`|         To retrieve all `/res` directories that will paticipate merge process.          |
|        ALL_JAVA_RES        |`ListProvider<RegularFile>`|               To retrieve all Java Resources that will paticipate merge process.               |

4. 另外，如果上述 API 集无法满足你的需求，Polyfill 提供了其底层的数据管道机制以及获取数据的便捷工具，方便注册自定义的工件（同样欢迎直接提交 PR）。

``` Kotlin
project.extensions.getByType<PolyfillExtension>()
    .registerPincerTaskConfig(DUMMY_SINGLE_ARTIFACT, DummySingleArtifactImpl::class)
```

更多信息请查看 `./test-plugin` 和`./polyfill/src/functionalTest`.


## 为什么需要 Polyfill？

顾名思义（Polyfill 直译为垫片），该框架是一个建立在 **AGP** (Android Gradle Plugin) 基础之上的，介于 **AGP** 和**第三方 Gradle Plugin** 之间的一个中间件。以 [ScratchPaper](https://github.com/2BAB/ScratchPaper) 项目为例，它是一个 Gradle 插件，基于 AGP 用于在 App 的启动图标上添加一层半透明信息，它需要这些输入：

1. SDK Locations / BuildToolInfo instance（用以运行 aapt2 命令）
2. 所有输入的 `res` 文件夹（用以查找启动图标文件来源）
3. 合并后的 AndroidManifest.xml 文件（用以获取解析后的图标名字）

在我刚创建 ScratchPaper 项目时，AGP 还未提供任何与上述三份数据有关的公开 API，我们只能使用一些骇客式的 Hooks 来解决。2018 年时，我开始思考是否可以为第三方 Android Gradle 插件开发者做一个 Polyfill 层（中间层），并且最终在 2020 年我发布了第一个版本，也即您在这所看到的。Polyfill 这个名字来自于前端技术栈，一个使 JS code 可以和一些老的/罕见的浏览器 API 兼容的库。

而从 AGP 7.0.0 开始，AGP 开发团队正式提供了一个新的公开 API 集，**"Artifacts"**。你可以从这里查看到最新公开的 Artifacts：[SingleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/SingleArtifact)
, [MultipleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifact)
（"Known Direct Subclasses" 的部分）。新的 `Variant/Artifact` 还处在较为早期的阶段，只提供了不到 10 个的 Artifacts API
给开发者们去使用。**由于 AGP 每年只发布 2-3 个小版本，开发者们需要紧跟更新，以期待获得自己的需求得到满足。** 回到上述案例，目前仅第三项数据是被 Artifacts API 所支持，剩余的两项则需要开发者自行处理。为了满足这些不被公开数据集支持的开发需求，我们能做的是：

1. 在 [AGP](https://issuetracker.google.com/issues?q=componentid:192709) 的 issues tracker 板块提出我们的需求。
2. 同时，构建一个非官方的数据管道用于承载我们的 hooks（借鉴 `artifacts.use()` 的机制），既作为临时的解决方案也方便未来过渡到官方的 Artifacts API。

这就是我为什么依然坚持去创造一个 Polyfill 库，并且希望有一天我们可以做到 100% 的迁移到 Artifacts API。你可从下方的链接获取更多的 Artifaces API 资讯：

- [gradle-recipes](https://github.com/android/gradle-recipes)：官方 Artifacts API 的展示案例。
- [New APIs in the Android Gradle Plugin](https://medium.com/androiddevelopers/new-apis-in-the-android-gradle-plugin-f5325742e614) ：一个简要的新 Artifacts API 介绍。
- [Extend the Android Gradle plugin](https://developer.android.com/studio/build/extend-agp)：Android 官方 2021 年 10 月放出的 Variant/Artifact API 官方文档。


## 兼容说明

Polyfill 只支持并在最新的两个 Android Gradle Plugin 版本进行测试。

| AGP Version | Latest Support Version |
|:-----------:|:----------------------:|
|    7.1.x    |         0.5.0          |
|    7.0.x    |         0.4.1          |
|    4.2.0    |  0.3.1 (MavenCentral)  |

（目前本工程基于 AGP 7.0 的最新版本进行开发，在 CI 环境下还会同时编译&测试 7.0/7.1 版本的兼容性）


## Git Commit Check

关于 Git Commit
的规则，请阅读这个 [link](https://medium.com/walmartlabs/check-out-these-5-git-tips-before-your-next-commit-c1c7a5ae34d1)，以确保自己写的是有意义的提交信息。

目前为止我还没有添加任何 git hook 工具，但是写 git commit message 时请遵守以下正则表达式：

```
(chore|feat|docs|fix|refactor|style|test|hack|release)(:)( )(.{0,80})
```

## License

>
> Copyright 2018-2022 2BAB
>
>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
>
>   http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
