package pw.prok.gradle.onesky.api.client

import groovy.transform.CompileStatic
import org.asynchttpclient.Request
import org.asynchttpclient.RequestBuilderBase
import org.asynchttpclient.SignatureCalculator

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@CompileStatic
class OneSkySignatureCalculator implements SignatureCalculator {
    private final String publicKey
    private final String secretKey

    OneSkySignatureCalculator(String publicKey, String secretKey) {
        this.publicKey = publicKey
        this.secretKey = secretKey
    }

    @Override
    void calculateAndAddSignature(Request request, RequestBuilderBase<?> requestBuilder) {
        def timestamp = System.currentTimeSeconds() as String
        def devHash = md5(timestamp, secretKey)
        requestBuilder.addQueryParam 'api_key', publicKey
        requestBuilder.addQueryParam 'timestamp', timestamp
        requestBuilder.addQueryParam 'dev_hash', devHash
    }

    private static String md5(String part1, String part2) {
        def digest = MessageDigest.getInstance 'MD5'
        digest.update part1.getBytes(StandardCharsets.UTF_8)
        digest.update part2.getBytes(StandardCharsets.UTF_8)
        digest.digest().encodeHex().toString()
    }
}
