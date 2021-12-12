<img src="./Polyfill.png" alt="Polyfill" width="507px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/polyfill/badge.svg)](https://search.maven.org/artifact/me.2bab/polyfill)
[![Actions Status](https://github.com/2bab/Polyfill/workflows/CI/badge.svg)](https://github.com/2bab/Polyfill/actions)
[![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[[English]](./README.md) [中文]

🚧 **目前还在孵化中...**

Polyfill 是一个构建系统的中间件，服务于编写 Android 构建环境下的 Gradle 插件。

## 为什么需要 Polyfill？

顾名思义（Polyfill 直译为垫片），该框架是一个建立在 **AGP** (Android Gradle Plugin) 基础之上的，介于 **AGP** 和**第三方 Gradle Plugin**
之间的一个中间件。以 [ScratchPaper](https://github.com/2BAB/ScratchPaper) 项目为例，它是一个 Gradle 插件，基于 AGP 用于在 App
的图标上添加一层半透明信息，它需要这些输入：

- SDK Locations / BuildToolInfo instance（用以运行 aapt 命令）
- 合并后的 AndroidManifest.xml 文件（用以获取解析后的图标名字）

但这些输入经常会引起如下问题：

1. AGP 是开源的但上述的一些输入所需 API 不是直接开放的，有时候你甚至需要去使用反射来获取你需要的输入；
2. 当你升级到新版本的 AGP 后，这些 API 经常会改变，因为这些输入信息主要是内部使用（在编写时就没有考虑外部的调用）。

2018 年的时候，我开始去思考我们是否可以为第三方Android Gradle 插件开发者做一个 Polyfill 层（中间层），并且最终在 2020 年我发布了第一个版本，也即您在这所看到的。Polyfill
这个名字来自于前端技术栈，它用于使 JS code 可以和一些老的/罕见的浏览器 API 兼容。

值得注意的是，从 AGP 4.1.0 开始，AGP 开发团队提供了一个新的公开 API 集，**"Artifacts"**。~~可惜，它现在仍处在预览和孵化的阶段，以至于他们只提供了不到 10 个的 artifact API
给开发者们去使用~~。**由于 AGP 每年只发布 2-3 个小版本，开发者们只能紧跟更新，以期待获得自己的需求得到满足。**

你可以从这里查看到最新公开的
Artifacts : [SingleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/SingleArtifact)
, [MultipleArtifact](https://developer.android.com/reference/tools/gradle-api/current/com/android/build/api/artifact/MultipleArtifact)
.（"Known Direct Subclasses" 的部分）

这就是我为什么依然坚持去创造一个 Polyfill 库，并且希望有一天我们可以做到 100% 的迁移到 Artifacts API。

可以从下面的链接获取更多的 Artifaces API 资讯

- [gradle-recipes](https://github.com/android/gradle-recipes): 官方 Artifacts API 的展示案例；
- [New APIs in the Android Gradle Plugin](https://medium.com/androiddevelopers/new-apis-in-the-android-gradle-plugin-f5325742e614) :
  一个简要的新 Artifacts API 介绍；
- [Extend the Android Gradle plugin](https://developer.android.com/studio/build/extend-agp): Android 官方 2021 年 10 月放出的
  Variant/Artifact API 官方文档。

## Polyfill 提供了什么？

- 第一，它封装了 AGP (Android Gradle Plugin) APIs，把它们转化成 **Task Hook Points** (Action) 和 **Task Inputs**
  ，使所有第三方插件开发者们能很容易地和它进行交互
    - **Task Hook Points:** 比如，如果开发者想要去拦截 manifest 合并输入的文件，需要找到输入文件的生产任务和消费任务，然后添加一个新的将要在这两者之间执行的自定义任务；这里，Hook Points
      就是指我们定义一个监听者，它处理任务执行顺序的事务，并且确保我们新添加的任务运行在正确的时间点。Polyfill 提供了很多`AGPTaskAction.kt` 的实现类
      ，比如 `ManifestBeforeMergeAction.kt`，去完成这项工作
    - **Task Inputs:** 有了 Hook Points 的帮助，自定义的任务逻辑现在可以被运行在正确的时间上，但是不要忘记添加 task input(s)，比如，合并的后的 Manifest 文件，Android SDK
      的本地路径，等等。为了使他们的配置变得更加简便，Polyfill 提供了多个 `SelfManageableProvider.kt` 的实现，比如`ManifestMergeInputProvider.kt`
      ，去抓取任务输入然后暴露给开发者调用。
- 第二，它提供了一些针对中间产物进行修改的工具，比如针对`resources.arsc`, `AndroidManifest.xml` 的二进制文件解析器，构建器。

## 快速上手

1. 在 build classpath 添加 Polyfill

``` kotlin
// If add to `buildSrc/build.gradle(.kts)` or standalone plugin project, 
// add them to your project's `dependencies{}` block:
dependencies {
    implementation("com.android.tools.build:gradle:7.0.3")
    // Core dependency 
    implementation("me.2bab:polyfill:0.4.1")
    // Add one or more artiface(s)-provider as you want ($latestVersion -> same as the core version)
    implementation("me.2bab:polyfill-manifest:$latestVersion")
    implementation("me.2bab:polyfill-res:$latestVersion")
    implementation("me.2bab:polyfill-arsc:$latestVersion")
    ...
}

// If you only extend the build inside `build.gradle(.kts)` script, 
// add them to Root project's `build.gradle.kts`.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        // Core dependency 
        classpath("me.2bab:polyfill:0.4.1")
        // Add one or more artiface(s)-provider as you want ($latestVersion -> same as the core version)
        classpath("me.2bab:polyfill-manifest:$latestVersion")
        classpath("me.2bab:polyfill-res:$latestVersion")
        classpath("me.2bab:polyfill-arsc:$latestVersion")
        ...
    }
}
```

2. 基于 Polyfill 编写一些自定义任务 (跟寻注释的步骤)

``` kotlin
val androidExtension = project.extensions.findByType(AndroidComponentsExtension::class.java)!!
androidExtension.onVariants { variant ->

    // 0. Get Polyfill instance with Project instance
    val polyfill = ApplicationVariantPolyfill(project, variant)

    // 1. Create & Config the hook task.
    val preUpdateTask = project.tasks.register(
        "preUpdate${variant.name.capitalize()}Manifest",
        TestPlugin.ManifestBeforeMergeTask::class.java
    ) {
        val p = polyfill.newProvider(ManifestMergeInputProvider::class.java).obtain()
        beforeMergeInputs.set(p)
    }
    // 2. Add it with the action (which plays the role of entry for a hook).
    val beforeMergeAction = ManifestBeforeMergeAction(preUpdateTask)
    polyfill.addAGPTaskAction(beforeMergeAction)
}

// Prepare a task containing specific hook logic.
abstract class ManifestBeforeMergeTask : DefaultTask() {
    @get:InputFiles
    abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

    @TaskAction
    fun beforeMerge() {
        val manifestPathsOutput = TestPlugin.getOutputFile(project, "manifests-merge-input.json")
        manifestPathsOutput.createNewFile()
        beforeMergeInputs.get().let { set ->
            manifestPathsOutput.writeText(JSON.toJSONString(set.map { it.asFile.absolutePath }))
        }
    }
}
```

更多信息可以查看 `./test-plugin` 和`./polyfill/src/functionalTest`.

## 兼容说明

Polyfill 只支持并在最新的两个 Android Gradle Plugin 版本进行测试。

| AGP Version | Latest Support Version |
|:-----------:|:----------------------:|
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
