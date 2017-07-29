package pw.prok.gradle.onesky.api.resource

import com.google.gson.annotations.SerializedName
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class FileDeleteResource {
    @SerializedName('file_name')
    String fileName
}
