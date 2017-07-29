package pw.prok.gradle.onesky.api.format

import groovy.transform.CompileStatic
import org.gradle.api.tasks.util.PatternFilterable
import pw.prok.gradle.onesky.api.ILocaleManager
import pw.prok.gradle.onesky.api.client.Formats

@CompileStatic
trait IFormat {
    @Override
    String toString() {
        formatName
    }

    String getFormatName() {
        Formats.values().find {
            it.format == this
        }?.name() ?: 'UNSUPPORTED'
    }

    abstract void applyDefaultPatterns(PatternFilterable patterns)

    abstract ILocaleManager getLocaleExtractor()

    abstract String getContentType(File file, String path)
}