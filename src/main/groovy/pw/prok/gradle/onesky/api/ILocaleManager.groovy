package pw.prok.gradle.onesky.api

import org.gradle.api.file.FileTreeElement

interface ILocaleManager {
    String extractLocale(FileTreeElement file)

    String applyLocale(String fileName, String locale)
}