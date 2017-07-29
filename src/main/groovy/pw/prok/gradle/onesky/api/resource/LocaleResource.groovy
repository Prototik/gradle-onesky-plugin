package pw.prok.gradle.onesky.api.resource

import com.google.gson.annotations.SerializedName
import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames = true, includePackage = false)
class LocaleResource {
    String code
    @SerializedName('english_name')
    String englishName
    @SerializedName('local_name')
    String localName
    String locale
    String region
}
