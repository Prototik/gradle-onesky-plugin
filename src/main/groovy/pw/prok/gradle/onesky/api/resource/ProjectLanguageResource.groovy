package pw.prok.gradle.onesky.api.resource

import com.google.gson.annotations.SerializedName
import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames = true, includePackage = false, includeSuperProperties = true)
class ProjectLanguageResource extends LocaleResource {
    @SerializedName('is_base_language')
    boolean baseLanguage
    @SerializedName('is_ready_to_publish')
    boolean readyToPublish
    @SerializedName('translation_progress')
    String translationProgress
    @SerializedName('uploaded_at')
    Date uploadedAt
    @SerializedName('uploaded_at_timestamp')
    long uploadedAtTimestamp

    float getPercentage() {
        def progress = translationProgress
        if (progress.endsWith('%')) progress = progress.substring(0, progress.length() - 1)
        progress as float
    }
}
