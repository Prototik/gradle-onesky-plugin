package pw.prok.gradle.onesky.api.client

import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import pw.prok.gradle.onesky.api.resource.Constants
import pw.prok.gradle.onesky.api.resource.OneSkyResponse
import pw.prok.gradle.onesky.api.resource.ProjectLanguageResource

import java.util.concurrent.CompletableFuture

import static pw.prok.gradle.onesky.api.resource.OneSkyResponseHandler.handler

@CompileStatic
class OneSkyProjectProvider {
    private static
    final TypeToken<List<ProjectLanguageResource>> LIST = new TypeToken<List<ProjectLanguageResource>>() {}
    private final OneSkyClient client

    OneSkyProjectProvider(OneSkyClient client) {
        this.client = client
    }

    CompletableFuture<OneSkyResponse<List<ProjectLanguageResource>>> listLanguages(long projectId) {
        client.http.prepareGet "${Constants.API_ENDPOINT}/projects/${projectId}/languages" execute handler(LIST) toCompletableFuture()
    }
}
