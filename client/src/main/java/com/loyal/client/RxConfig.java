package com.loyal.client;

import androidx.annotation.NonNull;

import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * https://www.jianshu.com/p/cf59500990c7
 */
public class RxConfig {

    private boolean trustedCert = true;//https方式访问,证书是否受信任
    private String protocol = "SSL";//哪种协议，如SSL协议或者TLS协议
    private String httpOrHttps = "http";//使用http还是https访问
    private String url = "192.168.0.1";//访问地址
    private String port = "";//端口地址 9080
    private String nameSpace;//访问服务名
    private OkHttpClient.Builder ctBuilder;
    private Cache cache;//缓存
    private List<Interceptor> interceptors;//应用拦截器
    private Interceptor netWorkInterceptor;//网络拦截器

    private static RxConfig rxConfig;

    //全局配置
    public static RxConfig getInstance() {
        if (rxConfig == null) {
            //同步锁
            synchronized (RxConfig.class) {
                if (rxConfig == null) {
                    rxConfig = new RxConfig();
                }
            }
        }
        return rxConfig;
    }

    //局部配置
    public RxConfig() {
    }

    public boolean isTrustedCert() {
        return trustedCert;
    }

    public void setTrustedCert(boolean trustedCert) {
        this.trustedCert = trustedCert;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHttpOrHttps() {
        return httpOrHttps;
    }

    public void setHttpOrHttps(String httpOrHttps) {
        this.httpOrHttps = httpOrHttps;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public OkHttpClient.Builder getOkCtBuilder() {
        return ctBuilder;
    }

    public void setOkCtBuilder(OkHttpClient.Builder builder) {
        this.ctBuilder = builder;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public Interceptor getNetWorkInterceptor() {
        return netWorkInterceptor;
    }

    public void setNetWorkInterceptor(Interceptor netWorkInterceptor) {
        this.netWorkInterceptor = netWorkInterceptor;
    }

    @NonNull
    @Override
    public String toString() {
        return "RxConfig{" +
                "httpOrHttps='" + httpOrHttps + '\'' +
                ", url='" + url + '\'' +
                ", port='" + port + '\'' +
                ", nameSpace='" + nameSpace + '\'' +
                ", trustedCert=" + trustedCert +
                ", protocol='" + protocol + '\'' +
                '}';
    }

}