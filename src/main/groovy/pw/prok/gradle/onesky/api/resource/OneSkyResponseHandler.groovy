package pw.prok.gradle.onesky.api.resource

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.AsyncHandler
import org.asynchttpclient.Response

import java.lang.reflect.Type

@CompileStatic
class OneSkyResponseHandler<T> extends AsyncCompletionHandler<OneSkyResponse<T>> {
    private static final Type RAW_TYPE = new TypeToken<OneSkyResponse<JsonElement>>() {}.type
    private static final Map<Type, OneSkyResponseHandler<?>> HANDLERS = new WeakHashMap<>()
    private final Type type

    OneSkyResponseHandler(Type type) {
        this.type = type
    }

    @Override
    OneSkyResponse<T> onCompleted(Response response) throws Exception {
        if (response.hasResponseBody() && response.contentType.startsWith('application/json')) {
            def rawResponse = Constants.GSON.fromJson(response.responseBody, RAW_TYPE) as OneSkyResponse<JsonElement>
            def responseData = Constants.GSON.fromJson(rawResponse.data, type) as T
            return new OneSkyResponse<T>(meta: rawResponse.meta, data: responseData)
        }
        new OneSkyResponse<T>(meta: new OneSkyResponse.Meta(status: response.statusCode))
    }

    static <T> AsyncHandler<OneSkyResponse<T>> handler(Type type) {
        def handler = HANDLERS[type]
        if (!handler) HANDLERS[type] = handler = new OneSkyResponseHandler<T>(type)
        handler as AsyncHandler<OneSkyResponse<T>>
    }

    static <T> AsyncHandler<OneSkyResponse<T>> handler(Class<T> clazz) {
        handler clazz as Type
    }

    static <T> AsyncHandler<OneSkyResponse<T>> handler(TypeToken<T> typeToken) {
        handler typeToken.type
    }

    static final AsyncHandler<OneSkyResponse<Void>> RAW_HANDLER = new AsyncCompletionHandler<OneSkyResponse<Void>>() {
        @Override
        OneSkyResponse<Void> onCompleted(Response response) throws Exception {
            def osr = new OneSkyResponse(meta: new OneSkyResponse.Meta(status: response.statusCode))
            if (response.hasResponseBody())
                osr.rawData = response.responseBody
            osr
        }
    }
}
