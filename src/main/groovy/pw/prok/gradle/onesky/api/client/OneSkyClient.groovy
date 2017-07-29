package pw.prok.gradle.onesky.api.client

import groovy.transform.CompileStatic
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl

@CompileStatic
class OneSkyClient {
    final AsyncHttpClient http

    OneSkyClient(String publicKey, String secretKey) {
        http = Dsl.asyncHttpClient Dsl.config().with {
            userAgent = 'OneSkyGradlePlugin/1.0'
            it
        } with {
            signatureCalculator = new OneSkySignatureCalculator(publicKey, secretKey)
            it
        }
    }

    final OneSkyFileProvider file = new OneSkyFileProvider(this)
    final OneSkyProjectProvider project = new OneSkyProjectProvider(this)
    final OneSkyTranslationsProvider translations = new OneSkyTranslationsProvider(this)
}
