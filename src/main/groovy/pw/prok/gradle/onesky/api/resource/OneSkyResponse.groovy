package pw.prok.gradle.onesky.api.resource

import com.google.gson.annotations.SerializedName
import groovy.transform.CompileStatic
import groovy.transform.ToString
import pw.prok.gradle.onesky.api.client.OneSkyException

@CompileStatic
@ToString(includeNames = true, includePackage = false)
final class OneSkyResponse<T> {
    Meta meta
    T data
    String rawData

    @ToString(includeNames = true, includePackage = false)
    static class Meta {
        int status
        String message
        @SerializedName('record_count')
        Integer recordCount
    }

    boolean isRaw() {
        rawData != null
    }

    boolean isSuccess() {
        meta && meta.status >= 200 && meta.status < 300
    }

    OneSkyResponse<T> validate() throws OneSkyException {
        if (!success)
            throw new OneSkyException(meta.message ?: 'Unknown error')
        this
    }
}
