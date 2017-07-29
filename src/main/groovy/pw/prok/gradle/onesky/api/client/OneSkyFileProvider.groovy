package pw.prok.gradle.onesky.api.client

import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import org.asynchttpclient.request.body.multipart.FilePart
import org.asynchttpclient.request.body.multipart.StringPart
import pw.prok.gradle.onesky.api.resource.*

import java.nio.charset.Charset
import java.util.concurrent.CompletableFuture

import static OneSkyResponseHandler.handler

@CompileStatic
class OneSkyFileProvider {
    private static final TypeToken<List<FileListResource>> LIST_PAGE = new TypeToken<List<FileListResource>>() {}
    private final OneSkyClient client

    OneSkyFileProvider(OneSkyClient client) {
        this.client = client
    }

    CompletableFuture<OneSkyResponse<List<FileListResource>>> listPage(long projectId, int page = 1, int perPage = 100) {
        client.http.prepareGet "${Constants.API_ENDPOINT}/projects/${projectId}/files" with {
            addQueryParam 'page', page as String
            addQueryParam 'per_page', perPage as String
        } execute handler(LIST_PAGE) toCompletableFuture()
    }

    CompletableFuture<List<FileListResource>> listAll(long projectId) {
        // TODO: make listing ALL
        listPage projectId, 1 thenApply {
            it.validate().data
        }
    }

    CompletableFuture<OneSkyResponse<FileUploadResource>> upload(long projectId, File file, String contentType, Charset charset,
                                                                 String fileName, String fileFormat, String locale = null,
                                                                 boolean keepingAllStrings = true,
                                                                 boolean allowTranslationSameAsOriginal = false) {
        client.http.preparePost "${Constants.API_ENDPOINT}/projects/${projectId}/files" with {
            addBodyPart new FilePart('file', file, contentType, charset, fileName)
            addBodyPart new StringPart('file_format', fileFormat)
            if (locale)
                addBodyPart new StringPart('locale', locale)
            addBodyPart new StringPart('is_keeping_all_strings', keepingAllStrings as String)
            addBodyPart new StringPart('is_allow_translation_same_as_original', allowTranslationSameAsOriginal as String)
        } execute handler(FileUploadResource) toCompletableFuture()
    }

    CompletableFuture<OneSkyResponse<FileDeleteResource>> delete(long projectId, String fileName) {
        client.http.prepareDelete "${Constants.API_ENDPOINT}/projects/${projectId}/files" with {
            addQueryParam 'file_name', fileName
        } execute handler(FileDeleteResource) toCompletableFuture()
    }
}
