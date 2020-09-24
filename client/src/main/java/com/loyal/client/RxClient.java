package com.loyal.client;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;

public class RxClient {
    private static RxClient mInstance;
    private OkHttpClient.Builder okctBuilder = new OkHttpClient.Builder()
            .connectTimeout(Timeout.TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .writeTimeout(Timeout.TIMEOUT_WRITE, TimeUnit.SECONDS)
            .readTimeout(Timeout.TIMEOUT_READ, TimeUnit.SECONDS)
            .pingInterval(Timeout.TIMEOUT_PING, TimeUnit.SECONDS)//websocket 心跳机制
            ;
    private OkHttpClient okClient = okctBuilder.build();
    private WebSocket mWebSocket;

    private RxClient() {
    }

    public static RxClient getInstance() {
        if (mInstance == null) {
            synchronized (RxClient.class) {
                if (mInstance == null) {
                    mInstance = new RxClient();
                }
            }
        }
        return mInstance;
    }

    public OkHttpClient.Builder getOkCtBuilder() {
        return okctBuilder;
    }

    public OkHttpClient getClient() {
        return okClient;
    }

    /**
     * 同步请求
     */
    public String requestSync(@NonNull String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return requestSync(request);
    }

    /**
     * 同步请求
     */
    public String requestSync(@NonNull Request request) throws IOException {
        Response response = okClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            return checkBody(body);
        } else throw new IOException("Unexpected code" + response);
    }

    /**
     * 重写了WebSocketListener中的几个方法，这几个方法很好理解，是用来异步回调的，
     * 这里简单说一下：onOpen当WebSocket和远程建立连接时回调；两个onMessage就是接收到消息时回调，
     * 只是消息内容的类型不同；onClosing是当远程端暗示没有数据交互时回调（即此时准备关闭，但连接还没有关闭）；
     * onClosed就是当连接已经释放的时候被回调；onFailure当然是失败时被回调（包括连接失败，发送失败等）。
     * <p>
     * send用来发送消息；close用来关闭连接
     *
     * @param wsUrl          访问的地址，如："ws://192.168.0.110/tracall/websocket/socketServer.do?sbxlh=123456";
     * @param socketListener socket连接监听
     */
    public void asSocketConnect(@NonNull String wsUrl, @NonNull WebSocketListener socketListener) {
        //建立连接
        Request request = new Request.Builder().url(wsUrl).build();
        asSocketConnect(request, socketListener);
    }

    private String checkBody(ResponseBody body) throws IOException {
        if (null == body)
            return "";
        return body.string();
    }

    /**
     * 使用之前必须用As
     */
    public void asSocketConnect(@NonNull Request request, @NonNull WebSocketListener socketListener) {
        //建立连接
        mWebSocket = okClient.newWebSocket(request, socketListener);
    }

    /**
     * @param rxConfig 访问参数配置类
     * @return http://192.168.0.155:9080/test/ 必须以"/"结尾
     */
    public static String getBaseUrl(@NonNull RxConfig rxConfig) {
        String httpOrHttps = rxConfig.getHttpOrHttps();
        String url = rxConfig.getUrl();
        String port = rxConfig.getPort();
        String nameSpace = rxConfig.getNameSpace();
        if (!portValid(port)) {
            throw new IllegalArgumentException("端口号范围0～65535，当前端口号：" + port);
        } else {
            String ipUrl =//端口号是否为空
                    TextUtils.isEmpty(port) ? url : String.format("%s:%s", url, port);
            if (ipUrl.endsWith("/"))
                ipUrl = ipUrl.substring(0, ipUrl.lastIndexOf("/"));

            String baseUrl;
            if (TextUtils.isEmpty(nameSpace)) {
                baseUrl = String.format("%s://%s/", httpOrHttps, ipUrl);
            } else
                baseUrl = String.format("%s://%s/%s/", httpOrHttps, ipUrl, nameSpace);
            return baseUrl;
        }
    }

    public static boolean portValid(String port) {
        try {
            if (TextUtils.isEmpty(port))
                return true;
            int portInt = Integer.parseInt(port);
            return (portInt >= 0 && portInt <= 65535);
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> void callExecute(Call<T> call, Callback<T> callback) {
        call.enqueue(callback);
    }

    /***/
    public void disConnect() {
        if (null != okClient) {
            okClient.dispatcher().executorService().shutdown();
        }
        if (null != mWebSocket) {
            mWebSocket.cancel();
        }
    }
}