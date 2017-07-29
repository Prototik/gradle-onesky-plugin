package pw.prok.gradle.onesky.api.resource

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.transform.CompileStatic

@CompileStatic
class Constants {
    static final Gson GSON = new GsonBuilder().with {
        dateFormat = 'yyyy-MM-dd\'T\'HH:mm:ssZZZZZ'
        create()
    }
    static final String API_ENDPOINT = 'https://platform.api.onesky.io/1'
}
