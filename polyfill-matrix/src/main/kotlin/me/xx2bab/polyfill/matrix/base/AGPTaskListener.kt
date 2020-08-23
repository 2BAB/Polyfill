package me.xx2bab.polyfill.matrix.base

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Incubating
import org.gradle.api.Project

/**
 * To provide Android Gradle Plugin (AGP) Task Hook Entry Points.
 * The base listener here defines two variant-traversal and project evaluated callbacks
 * as general entrances for plugins.
 *
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
interface AGPTaskListener {

    /**
     * Executes after project has been evaluated, in case of something need to be executed without
     * multiple variant calls or some custom logic.
     */
    fun onProjectEvaluated(project: Project,
                           androidExtension: DomainObjectSet<out BaseVariant>)

    /**
     * Executes on Gradle Configuration stage which is provided by Android Gradle Plugin.
     * Will be invoked multiple times.
     */
    @Incubating
    fun onVariantProperties(project: Project,
                            androidExtension: CommonExtension<*, *, *, *, *, *, *, *>,
                            variant: VariantProperties,
                            variantCapitalizedName: String)

    /**
     * Executes on Gradle Configuration stage while the variant has been configured.
     * Will be invoked multiple times.
     */
    fun onVariantClassicProperties(project: Project,
                                   androidExtension: BaseExtension,
                                   variant: BaseVariant,
                                   variantCapitalizedName: String)

}