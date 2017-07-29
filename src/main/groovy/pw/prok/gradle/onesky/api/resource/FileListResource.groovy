package pw.prok.gradle.onesky.api.resource

import com.google.gson.annotations.SerializedName
import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames = true, includePackage = false)
class FileListResource {
    @SerializedName('file_name')
    String fileName
    @SerializedName('string_count')
    long stringCount
    @SerializedName('last_import')
    LastImport lastImport
    @SerializedName('uploaded_at')
    Date uploadedAt
    @SerializedName('uploaded_at_timestamp')
    long uploadedAtTimestamp

    @ToString(includeNames = true, includePackage = false)
    static class LastImport {
        long id
        Status status

        enum Status {
            @SerializedName('completed') Completed,
            @SerializedName('in-progress') InProgress,
            @SerializedName('failed') Failed
        }
    }
}
