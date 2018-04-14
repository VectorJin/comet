package com.vector.comet.util.http;

import com.vector.comet.constants.BaseConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by jinku on 2018/3/31.
 */
public class AsyncHttpClient {

    private static AsyncHttpClient instance = new AsyncHttpClient();

    private AsyncHttpClient() {
        try {
            init();
        } catch (Exception e) {
            throw new Error("AsyncHttpClient init exception", e);
        }
    }

    private CloseableHttpAsyncClient httpclient = null;

    public static AsyncHttpClient getInstance() {
        return instance;
    }

    private void init() throws IOReactorException {
        // Create I/O reactor configuration
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setConnectTimeout(BaseConstants.TIMEOUT_SECONDS * 1000)
                .setSoTimeout(BaseConstants.TIMEOUT_SECONDS * 1000)
                .build();

        // Create a custom I/O reactort
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(100);

        HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom();
        RequestConfig.Builder requestConfig = RequestConfig.custom();
        // 连接超时,连接建立时间
        requestConfig.setConnectTimeout(BaseConstants.TIMEOUT_SECONDS * 1000);
        // 请求超时,数据传输过程中数据包之间间隔的最大时间
        requestConfig.setSocketTimeout(BaseConstants.TIMEOUT_SECONDS * 1000);
        // 使用连接池来管理连接,从连接池获取连接的超时时间
        requestConfig.setConnectionRequestTimeout(BaseConstants.TIMEOUT_SECONDS * 1000);
        clientBuilder.setConnectionManager(connManager);
        clientBuilder.setDefaultRequestConfig(requestConfig.build());
        httpclient = clientBuilder.build();
    }

    public Future<HttpResponse> asyncPost(String url, Map<String, String> paramsMap,
                                          final HttpCallback callback) throws Exception {
        final HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            paramsList.add(new BasicNameValuePair(key, value));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(paramsList, "UTF-8"));
        return httpclient.execute(httpPost, new FutureCallback<HttpResponse>(){
            public void completed(HttpResponse httpResponse) {
                if(httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    try {
                        String response = EntityUtils.toString(httpEntity);
                        callback.onSuccess(response);
                    } catch (Exception e) {
                        // TODO log
                        callback.onFailed(e);
                    }
                }
            }

            public void failed(Exception e) {
                callback.onFailed(e);
            }

            public void cancelled() {
                callback.onFailed(null);
            }
        });
    }

    public void close() {
        if (httpclient != null) {
            try {
                httpclient.close();
            } catch (Exception e) {
            }
        }
    }

}
