package com.loyal.client.download;

/**
 * 下载进度listener
 */
public interface DownloadImpl {
    void update(long bytesRead, long contentLength, boolean done);

    void error(Throwable error);
}
