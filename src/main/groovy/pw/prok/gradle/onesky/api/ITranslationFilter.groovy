package pw.prok.gradle.onesky.api

import pw.prok.gradle.onesky.api.resource.ProjectLanguageResource

interface ITranslationFilter {
    boolean acceptableTranslation(ProjectLanguageResource projectLanguage)
}