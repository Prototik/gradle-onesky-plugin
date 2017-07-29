package pw.prok.gradle.onesky.tasks

import groovy.transform.CompileStatic
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import pw.prok.gradle.onesky.api.ITranslationFilter
import pw.prok.gradle.onesky.api.resource.FileListResource
import pw.prok.gradle.onesky.api.resource.OneSkyResponse
import pw.prok.gradle.onesky.api.resource.ProjectLanguageResource

import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.Supplier

import static java.util.concurrent.CompletableFuture.completedFuture

@CompileStatic
class PullTask extends OneSkyTask {
    @OutputDirectory
    File destinationDir
    ITranslationFilter translationFilter

    @TaskAction
    void pull() {
        def locales = client.project.listLanguages projectId get() validate() data
        locales = locales.findAll {
            !it.baseLanguage && (!translationFilter || translationFilter.acceptableTranslation(it))
        }
        if (logger.infoEnabled) logger.info 'Candidate locales: {}', locales*.code

        List<CompletableFuture<?>> futures = []

        def files = client.file.listAll projectId get()
        files.each { FileListResource file ->
            locales.each { ProjectLanguageResource locale ->
                String exportFileName = localeManager.applyLocale(file.fileName, locale.code)
                logger.info 'Pulling {} for locale {} as {}', file.fileName, locale.code, exportFileName
                futures << repeat(new Supplier<CompletableFuture<OneSkyResponse<Void>>>() {
                    @Override
                    CompletableFuture<OneSkyResponse<Void>> get() {
                        client.translations.translation projectId, locale.code, file.fileName, exportFileName
                    }
                }, new Predicate<OneSkyResponse<Void>>() {
                    @Override
                    boolean test(OneSkyResponse<Void> response) {
                        if (response.meta.status == 200) {
                            logger.info 'Got response for translation {}', exportFileName
                            return true
                        } else if (response.meta.status == 202) {
                            logger.info 'Export task for {} in progress, trying one more time...', exportFileName
                            return false
                        } else if (response.meta.status == 204) {
                            logger.info 'Got empty response for translation {}', exportFileName
                            return true
                        } else {
                            logger.error 'Unknown status code {} for translation file {}', response.meta.status, exportFileName
                            return false
                        }
                    }
                }).thenAccept(new Consumer<OneSkyResponse<Void>>() {
                    @Override
                    void accept(OneSkyResponse<Void> response) {
                        if (response.meta.status == 200) {
                            def exportFile = new File(destinationDir, exportFileName)
                            exportFile.parentFile.mkdirs()
                            exportFile.withWriter charset, {
                                it.write response.rawData
                            }
                            logger.info 'Pulled {} as {}', exportFileName, exportFile.canonicalPath
                        }
                    }
                })
            }
        }

        futures*.join()
        logger.info 'Pulling translations for project {} completed', projectId
    }

    static <T> CompletableFuture<T> repeat(Supplier<CompletableFuture<T>> action, Predicate<T> until) {
        action.get().thenComposeAsync { result ->
            until.test(result) ? completedFuture(result) : repeat(action, until)
        }
    }
}
