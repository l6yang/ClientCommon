package com.loyal.client;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroCallManager {

    private static final String TAG = RetroCallManager.class.getSimpleName();
    public static boolean logOut = true;
    private Retrofit retrofit;
    private OkHttpClient.Builder clientBuilder = RxClient.getInstance().getOkCtBuilder();
    private static RetroCallManager mInstance;

    private RetroCallManager() {
    }

    public static RetroCallManager getInstance() {
        if (mInstance == null) {
            synchronized (RetroCallManager.class) {
                if (mInstance == null)
                    mInstance = new RetroCallManager();
            }
        }
        return mInstance;
    }

    public void build(@NonNull RxConfig config) {
        initClient(config);
        //
        init(config);
    }

    private void initClient(RxConfig config) {
        boolean trustedCert = config.isTrustedCert();
        String protocol = config.getProtocol();
        OkHttpClient.Builder ctBuilder = config.getOkCtBuilder();
        if (null != ctBuilder)
            this.clientBuilder = ctBuilder;
        clientBuilder.interceptors().clear();

        //新建log拦截器
        addLogging();
        checkCert(trustedCert, protocol);
        List<Interceptor> interceptorList = config.getInterceptors();
        //应用拦截器
        if (null != interceptorList && !interceptorList.isEmpty()) {
            for (Interceptor interceptor : interceptorList) {
                clientBuilder.addInterceptor(interceptor);
                clientBuilder.interceptors().clear();
            }
        }
        Cache cache = config.getCache();
        if (null != cache)
            clientBuilder.cache(cache);
        Interceptor netWorkInter = config.getNetWorkInterceptor();
        //网络拦截器，可以实现缓存机制
        if (null != netWorkInter)
            clientBuilder.addNetworkInterceptor(netWorkInter);
    }

    private void init(@NonNull RxConfig config) {
        String baseUrl = RxClient.getBaseUrl(config);

        if (TextUtils.isEmpty(baseUrl))
            baseUrl = "http://192.168.0.1/";

        Retrofit.Builder retBuilder = new Retrofit.Builder().client(clientBuilder.build())
                .baseUrl(baseUrl)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create());

        CallAdapter.Factory factory = factory();
        if (null != factory)
            retBuilder.addCallAdapterFactory(factory);

        retrofit = retBuilder.build();
    }

    private void checkCert(boolean trustedCert, String protocol) {
        if (!trustedCert) {//若证书不信任执行这里，信任的话流程就和http访问方式一样
            try {
                SSLContext sc = SSLContext.getInstance(protocol);
                sc.init(null, trustManager, /*new SecureRandom()*/null);
                clientBuilder
                        .sslSocketFactory(sc.getSocketFactory(), (X509TrustManager) trustManager[0])
                        .hostnameVerifier(hostnameVerifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T createServer(Class<T> tClass) {
        return retrofit.create(tClass);
    }

    public CallAdapter.Factory factory() {
        return null;
    }

    /**
     * 添加日志拦截
     */
    private void addLogging() {
        if (!logOut) {
            return;
        }
        HttpLoggingInterceptor interceptorBody = new HttpLoggingInterceptor(message -> Log.e(TAG, message));
        interceptorBody.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(interceptorBody);//日志拦截
    }

    @SuppressLint("TrustAllX509TrustManager")
    private TrustManager[] trustManager = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }};

    @SuppressLint("BadHostnameVerifier")
    private HostnameVerifier hostnameVerifier = (hostname, session) -> true;

    public String baseUrl() {
        return retrofit.baseUrl().toString();
    }

}