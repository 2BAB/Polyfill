package me.xx2bab.polyfill.base

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Incubating
import org.gradle.api.Project

/**
 * To provide Android Gradle Plugin (AGP) Task Hook Entry Points. If a task action implement
 * this interface directly, means it supports both Application and Library, or should
 * use [ApplicationAGPTaskAction] and [LibraryAGPTaskAction] instead.
 *
 * The base action here defines a new variant-traversal approach with AndroidComponentsExtension<*, *>.
 * Below is a quote from the article of Android Developer Medium, describes the difference of
 * 2 variant callbacks:
 *
 * The new variant API runs much earlier during configuration than the previous API.
 * This allows the variants to be modified in ways that affect the build flow,
 * unlike the previous API where those decisions had already been made by the time the API ran.
 * These changes can be both explicit, allowing setting properties that affect the build flow,
 * but also implicit, if there is an optimization that is incompatible with something exposed
 * in the variant API, it can simply be done conditionally on the use of that API.
 *
 * @see [New APIs in the Android Gradle Plugin](https://medium.com/androiddevelopers/new-apis-in-the-android-gradle-plugin-f5325742e614)
 */
interface AGPTaskAction {

    /**
     * Executes on Gradle Configuration stage which is provided by Android Gradle Plugin.
     * Will be invoked multiple times.
     */
    @Incubating
    fun orchestrate(project: Project,
                    androidExtension: AndroidComponentsExtension<*, *, *>,
                    variant: Variant,
                    variantCapitalizedName: String)

}