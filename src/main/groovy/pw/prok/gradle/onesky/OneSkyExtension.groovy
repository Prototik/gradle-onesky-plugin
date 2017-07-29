package pw.prok.gradle.onesky

import groovy.transform.CompileStatic
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import pw.prok.gradle.onesky.api.ILocaleManager
import pw.prok.gradle.onesky.api.ITranslationFilter
import pw.prok.gradle.onesky.api.client.Formats
import pw.prok.gradle.onesky.api.format.IFormat

@CompileStatic
class OneSkyExtension implements PatternFilterable {
    long projectId
    String publicKey
    String secretKey
    IFormat format
    ILocaleManager localeManager
    @Delegate(includeTypes = PatternFilterable, interfaces = false)
    final PatternSet patterns = new PatternSet()
    List<Object> sources = []
    Object destinationDir
    ITranslationFilter translationFilter
    boolean pullOnBuild = true
    String charset = 'utf-8'

    void source(source) {
        sources << source
    }

    void setFormat(IFormat format) {
        this.format = format
    }

    void setFormat(String format) {
        this.format = Formats.valueOf(format).format
    }
}
