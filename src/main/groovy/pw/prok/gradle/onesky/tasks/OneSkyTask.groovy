package pw.prok.gradle.onesky.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.Factory
import pw.prok.gradle.onesky.api.ILocaleManager
import pw.prok.gradle.onesky.api.client.OneSkyClient
import pw.prok.gradle.onesky.api.format.IFormat

import javax.inject.Inject

@CompileStatic
class OneSkyTask extends DefaultTask {
    final PatternFilterable patterns = patternSetFactory.create()
    OneSkyClient client
    @Input
    long projectId
    IFormat format
    ILocaleManager localeManager
    @Input
    String charset

    @Inject
    protected Factory<PatternSet> getPatternSetFactory() {
        throw new UnsupportedOperationException()
    }

    void setPatterns(PatternFilterable patterns) {
        (this.patterns as PatternSet).copyFrom patterns
    }
}
