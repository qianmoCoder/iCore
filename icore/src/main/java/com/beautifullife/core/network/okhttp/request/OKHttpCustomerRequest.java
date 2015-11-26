package com.beautifullife.core.network.okhttp.request;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okio.BufferedSink;

/**
 * Created by admin on 2015/11/16.
 */
public class OKHttpCustomerRequest extends OKHttpRequest {

    public static final MediaType MEDIA_TYPE_APP_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_APP_OCTET_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");

    public static final MediaType MEDIA_TYPE_TXT_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_TXT_X_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");

    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public static final int MEDIA_TYPE_MARKDOWN = 0x01;


    private final String context;

    private OKHttpCustomerRequest(Builder builder) {
        super(builder);
        this.context = builder.context;
    }

    public static class Builder extends OKHttpRequest.Builder<Builder> {

        private String context;

        public Builder context(String context) {
            this.context = context;
            return this;
        }

        @Override
        public OKHttpCustomerRequest build() {
            return new OKHttpCustomerRequest(this);
        }
    }

    public static Builder create() {
        return new Builder();
    }

    @Override
    public Request buildRequest() {
        Request.Builder builder = getBuilder();
        String url = getUrl();
        builder.url(url);
        builder.post(buildeRequestBody());
        return builder.build();
    }

    private RequestBody buildeRequestBody() {
        return null;
    }

    public String getContext() {
        return context;
    }

    public RequestBody postString() throws IOException {
//        String postBody = "" + "Releases\n" + "--------\n" + "\n" + " * _1.0_ May 6, 2013\n" + " * _1.1_ June 15, 2013\n" + " * _1.2_ August 11, 2013\n";
        RequestBody formBody = RequestBody.create(MEDIA_TYPE_TXT_X_MARKDOWN, getContext());
        return formBody;
    }

    public RequestBody postStream() throws Exception {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_TXT_X_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 997; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n)
                        return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };
        return requestBody;
    }

    public RequestBody postFile() throws IOException {
        File file = new File("README.md");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_TXT_X_MARKDOWN, file);
        return requestBody;
    }

    public RequestBody postFormParameters() {
        RequestBody formBody = new FormEncodingBuilder().add("search", "Jurassic Park").build();
        return formBody;
    }

    private static final String IMGUR_CLIENT_ID = "...";
    ;

    public RequestBody postMultipartRequest() {
        RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM).addPart(Headers.of("Content-Disposition", "form-data; name=\"title\""), RequestBody.create(null, "Square Logo"))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"image\""), RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png"))).build();

        // RequestBody requestBodyTemp = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("hello", "android").addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
        // .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), file)).build();

//        Request request = new Request.Builder().header("Authorization", "Client-ID " + IMGUR_CLIENT_ID).url("https://api.imgur.com/3/image").post(requestBody).build();
        return requestBody;
    }

    public void uploadFile(Map<String, String> map, List<String> paths) {
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (String key : map.keySet()) {
            builder.addFormDataPart(key, map.get(key));
        }

        for (String path : paths) {
            builder.addFormDataPart("upload", null, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
        }
    }

    // OAuth
    private Call buildCall(Request request) {
//		OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer("CONSUMER_KEY","CONSUMER_SECRET");
//		consumer.setTokenWithSecret("token", "secret");
//
//		mOkHttpClient.interceptors().add(new SigningInterceptor(consumer));
//		request = (Request)consumer.sign(request).unwrap();
        Call call = okHttpManager.getOkHttpClient().newCall(request);
        return call;
    }
}
