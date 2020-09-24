package com.loyal.client.download;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressInterceptor implements Interceptor {

    private DownloadImpl listener;

    public ProgressInterceptor(DownloadImpl listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), listener))
                .build();
    }

    private static class DownloadProgressResponseBody extends ResponseBody {

        private ResponseBody responseBody;
        private DownloadImpl progressListener;
        private BufferedSource bufferedSource;

        DownloadProgressResponseBody(ResponseBody responseBody,
                                     DownloadImpl progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @NonNull
        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                    try {
                        long bytesRead = super.read(sink, byteCount);
                        totalBytesRead += bytesRead != -1L ? bytesRead : 0L;
                        if (null != progressListener) {
                            progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L);
                        }
                        return bytesRead;
                    } catch (Exception e) {
                        if (null != progressListener) {
                            progressListener.error(e);
                        }
                        return 0L;
                    }
                }
            };
        }
    }
}
