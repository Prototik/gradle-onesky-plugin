package pw.prok.gradle.onesky.tasks

import groovy.transform.CompileStatic
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import pw.prok.gradle.onesky.api.resource.FileDeleteResource
import pw.prok.gradle.onesky.api.resource.FileListResource
import pw.prok.gradle.onesky.api.resource.OneSkyResponse

import java.nio.charset.Charset
import java.util.concurrent.CompletableFuture

@CompileStatic
class PushTask extends OneSkyTask {
    List<Object> sources = []
    @Input
    boolean keepAllStrings = true
    @Input
    boolean allowTranslationSameAsOriginal = false
    @Input
    boolean deleteOrphans = true
    @Input
    boolean undeletableFatal = true

    void source(source) {
        sources << source
    }

    @InputFiles
    FileTree getSource() {
        project.files(sources).asFileTree.matching(patterns)
    }

    @TaskAction
    void push() {
        def serverFiles = client.file.listAll projectId get()
        if (logger.infoEnabled) {
            logger.info 'Got file list for project {}: {}', projectId, serverFiles.collect { it.fileName }
        }
        Map<FileTreeElement, String> toUpdate = [:]
        source.visit { FileVisitDetails file ->
            if (file.directory) return
            def locale = localeManager.extractLocale file
            if (locale == null) {
                toUpdate[file] = locale
                logger.info 'File {} treated as base locale and need to be pushed', file.path
            } else {
                logger.info 'File {} treated as translation {} and avoid pushing', file.path, locale
            }
        }

        List<CompletableFuture<?>> futures = []

        if (deleteOrphans) {
            serverFiles.findAll { FileListResource server ->
                toUpdate.every { Map.Entry<FileTreeElement, String> local -> local.key.path != server.fileName }
            } each { resource ->
                logger.info 'Deleting resource "{}"', resource.fileName
                futures << client.file.delete(projectId, resource.fileName).thenAccept { OneSkyResponse<FileDeleteResource> response ->
                    if (undeletableFatal) {
                        response.validate()
                    } else if (!response.success) {
                        logger.warn 'Unable to delete file "{}" due to error: {}', resource.fileName, response.meta.message
                    }
                }
            }
            futures*.join()
            futures.clear()
            logger.info 'Deleting orphan resources for project {} completed', projectId
        }

        toUpdate.each {
            logger.info 'Pushing resource "{}" with locale {} from file "{}"...', it.key.path, it.value, it.key.file
            futures << client.file.upload(projectId,
                    it.key.file, format.getContentType(it.key.file, it.key.path), Charset.forName(charset), it.key.path,
                    format.formatName, it.value, keepAllStrings, allowTranslationSameAsOriginal)
        }

        futures*.join()
        logger.info 'Pushing new or modified resources for project {} completed', projectId
    }
}
