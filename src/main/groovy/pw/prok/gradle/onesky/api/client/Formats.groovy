package pw.prok.gradle.onesky.api.client

import groovy.transform.CompileStatic
import pw.prok.gradle.onesky.api.format.IFormat
import pw.prok.gradle.onesky.api.format.JavaPropertiesFormat

@CompileStatic
enum Formats {
    JAVA_PROPERTIES(JavaPropertiesFormat.instance)

    final IFormat format

    Formats(IFormat format) {
        this.format = format
    }
}
