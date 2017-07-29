package pw.prok.gradle.onesky.api.format

import groovy.transform.CompileStatic
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.util.PatternFilterable
import pw.prok.gradle.onesky.api.ILocaleManager

@Singleton
@CompileStatic
class JavaPropertiesFormat implements IFormat {
    @Override
    void applyDefaultPatterns(PatternFilterable patterns) {
        patterns.include '*.properties'
    }

    @Override
    ILocaleManager getLocaleExtractor() {
        ResourceBundleLocaleManager.instance
    }

    @Override
    String getContentType(File file, String path) {
        'text/x-java-properties'
    }

    @Singleton
    static final class ResourceBundleLocaleManager implements ILocaleManager {
        @Override
        String extractLocale(FileTreeElement file) {
            def name = file.name
            def start = name.indexOf '_'
            if (start > 0) {
                def end = name.lastIndexOf '.'
                return name.substring(start + 1, end).replace('_', '-')
            }
            null
        }

        @Override
        String applyLocale(String name, String locale) {
            StringBuilder builder = new StringBuilder()
            def start = name.indexOf '_'
            def point = name.lastIndexOf '.'
            if (start > 0) {
                builder.append(name.substring(0, start + 1))
            } else {
                builder.append(name.substring(0, point)).append('_')
            }
            builder.append(locale.replace('-', '_'))
                    .append(name.substring(point))
            builder as String
        }
    }
}
