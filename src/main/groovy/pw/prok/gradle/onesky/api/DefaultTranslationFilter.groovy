package pw.prok.gradle.onesky.api

import groovy.transform.CompileStatic
import pw.prok.gradle.onesky.api.resource.ProjectLanguageResource

@Singleton
@CompileStatic
final class DefaultTranslationFilter implements ITranslationFilter {
    @Override
    boolean acceptableTranslation(ProjectLanguageResource language) {
        !language.baseLanguage && language.readyToPublish
    }
}
