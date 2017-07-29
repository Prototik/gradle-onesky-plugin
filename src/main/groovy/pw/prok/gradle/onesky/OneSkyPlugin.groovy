package pw.prok.gradle.onesky

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.language.jvm.tasks.ProcessResources
import pw.prok.gradle.onesky.api.DefaultTranslationFilter
import pw.prok.gradle.onesky.api.client.OneSkyClient
import pw.prok.gradle.onesky.tasks.PullTask
import pw.prok.gradle.onesky.tasks.PushTask

@CompileStatic
class OneSkyPlugin implements Plugin<Project> {
    private OneSkyExtension extension
    private PushTask pushTask
    private PullTask pullTask

    @Override
    void apply(Project project) {
        extension = project.extensions.create 'onesky', OneSkyExtension
        pushTask = project.tasks.create 'pushOneSky', PushTask
        pullTask = project.tasks.create 'pullOneSky', PullTask

        project.afterEvaluate {
            def javaConvention = project.convention.getPlugin(JavaPluginConvention)
            def processResources = project.tasks.findByName(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) as ProcessResources

            if (!extension.format)
                misconfiguration 'format required'
            if (!extension.publicKey)
                misconfiguration 'public key required'
            if (!extension.secretKey)
                misconfiguration 'secret key required'

            def client = new OneSkyClient(extension.publicKey, extension.secretKey)

            if (extension.patterns.empty)
                extension.format.applyDefaultPatterns extension.patterns

            def localeManager = extension.localeManager ?: extension.format.localeExtractor
            def sources = [*extension.sources]

            if (sources.empty) {
                javaConvention?.with { convention ->
                    convention.sourceSets.findByName SourceSet.MAIN_SOURCE_SET_NAME
                }?.with { sourceSet ->
                    sources.addAll sourceSet.resources.srcDirs
                }
            }

            def destinationDir = project.file(extension.destinationDir ?: processResources?.destinationDir)

            [pushTask, pullTask].each {
                it.client = client
                it.projectId = extension.projectId
                it.format = extension.format
                it.patterns = extension.patterns
                it.localeManager = localeManager
                it.charset = extension.charset
            }

            pushTask.sources = sources

            pullTask.destinationDir = destinationDir
            pullTask.translationFilter = extension.translationFilter ?: DefaultTranslationFilter.instance
            pullTask.outputs.upToDateWhen { false }

            if (extension.pullOnBuild && processResources) {
                processResources.dependsOn pullTask
            }
        }
    }

    private static void misconfiguration(String message) {
        throw new RuntimeException("Misconfiguration of oneskyapp plugin: ${message}")
    }

}
