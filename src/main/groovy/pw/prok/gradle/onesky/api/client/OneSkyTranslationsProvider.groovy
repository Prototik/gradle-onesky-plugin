package pw.prok.gradle.onesky.api.client

import pw.prok.gradle.onesky.api.resource.Constants
import pw.prok.gradle.onesky.api.resource.OneSkyResponse
import pw.prok.gradle.onesky.api.resource.OneSkyResponseHandler

import java.util.concurrent.CompletableFuture

class OneSkyTranslationsProvider {
    private final OneSkyClient client

    OneSkyTranslationsProvider(OneSkyClient client) {
        this.client = client
    }

    CompletableFuture<OneSkyResponse<Void>> translation(long projectId, String locale, String sourceFileName, String exportFileName = null) {
        client.http.prepareGet "${Constants.API_ENDPOINT}/projects/${projectId}/translations" with {
            addQueryParam 'locale', locale
            if (exportFileName) addQueryParam 'export_file_name', exportFileName
            addQueryParam 'source_file_name', sourceFileName
        } execute OneSkyResponseHandler.RAW_HANDLER toCompletableFuture()
    }
}
