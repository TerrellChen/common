package terrell.common.http;
/**
 * @author: TerrellChen
 * @version: Created in 下午8:11 2/4/19
 */

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrell.common.http.model.FetchData;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 */
public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CloseableHttpClient httpClient;
    private static CloseableHttpClient httpsClient;
    private static CloseableHttpClient fooClient;
    private static int maxPool = 50;
    private static int maxPerRoute = 5;
    private static int socketTimeout = 30000;
    private static int connectTimeout = 5000;
    private static final String fooUsername = "";
    private static final String fooUpassword = "";
    private static final String fooDomain = "";

    static {
        PoolingHttpClientConnectionManager connoManager = new PoolingHttpClientConnectionManager();
        connoManager.setMaxTotal(maxPool);//整个连接池最大连接数
        connoManager.setDefaultMaxPerRoute(maxPerRoute);//每路由最大连接数，默认值是2
        connoManager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Charset.forName("utf-8")).build());
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();//设置请求和传输超时时间

        httpsClient =  HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(connoManager).build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(connoManager).build();
        CredentialsProvider provider = new BasicCredentialsProvider();
        // Create the authentication scope
        AuthScope scope = new AuthScope(fooDomain, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        // Create credential pair，在此处填写用户名和密码
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(fooUsername, fooUpassword);
        // Inject the credentials
        provider.setCredentials(scope, credentials);
        fooClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(connoManager).setDefaultCredentialsProvider(provider).build();
    }

    public static CloseableHttpClient getHttpClient(int connectTimeout, int socketTimeout){
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }


    public static FetchData get(String scheme, String host, String path, Map<String, String> params, Map<String, String>headers){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUriWithParams(scheme, host, path, params);
            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);
            headers.forEach((k,v) -> {
                httpGet.setHeader(k,v);
            });
            return doRequest(httpClient,httpGet,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData get(String scheme, String host, String path, Map<String, String>params){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUriWithParams(scheme, host, path, params);
            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);
            return doRequest(httpClient,httpGet,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData get(String scheme, String host, String path){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);
            return doRequest(httpClient,httpGet,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData post(String scheme, String host, String path, Map<String, String>params, Map<String, String>headers){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(uri);

            httpPost.setEntity(new UrlEncodedFormEntity(generateNameValiePair(params), "UTF-8"));

            headers.forEach((k,v) -> {
                httpPost.setHeader(k,v);
            });

            return doRequest(httpClient,httpPost,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }catch (UnsupportedEncodingException e){
            logger.error("参数编码失败: " + e);
            return null;
        }
    }

    public static FetchData post(String scheme, String host, String path, Map<String, String>params){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(uri);

            httpPost.setEntity(new UrlEncodedFormEntity(generateNameValiePair(params), "UTF-8"));

            return doRequest(httpClient,httpPost,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }catch (UnsupportedEncodingException e){
            logger.error("参数编码失败: " + e);
            return null;
        }
    }

    public static FetchData post(String scheme, String host, String path, Object object, Map<String, String> params, Map<String,String> headers){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUriWithParams(scheme, host, path, params);
            URI uri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(uri);

            String json = JSON.toJSONString(object);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));

            headers.forEach((k,v) -> {
                httpPost.setHeader(k,v);
            });

            httpPost.addHeader("Content-type","application/json;");

            return doRequest(httpClient,httpPost,uri);
        }catch (URISyntaxException e) {
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData post(String scheme, String host, String path, Object object){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(uri);

            String json = JSON.toJSONString(object);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
            httpPost.addHeader("Content-type","application/json;");

            return doRequest(httpClient,httpPost,uri);
        }catch (URISyntaxException e) {
            logger.error("url解析失败: " + e);
            return null;
        }
    }


    public static FetchData post(String scheme, String host, String path){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(uri);
            return doRequest(httpClient,httpPost,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData delete(String scheme, String host, String path, Map<String, String> params, Map<String, String> headers){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUriWithParams(scheme, host, path, params);
            URI uri = uriBuilder.build();
            HttpDelete httpDelete = new HttpDelete(uri);

            headers.forEach((k,v) -> {
                httpDelete.setHeader(k,v);
            });

            return doRequest(httpClient,httpDelete,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData delete(String scheme, String host, String path, Map<String, String>params){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUriWithParams(scheme, host, path, params);

            URI uri = uriBuilder.build();
            HttpDelete httpDelete = new HttpDelete(uri);
            return doRequest(httpClient,httpDelete,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData delete(String scheme, String host, String path){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);

            URI uri = uriBuilder.build();
            HttpDelete httpDelete = new HttpDelete(uri);
            return doRequest(httpClient,httpDelete,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData put(String scheme, String host, String path, Object object, Map<String, String> params, Map<String,String> headers){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUriWithParams(scheme, host, path, params);
            URI uri = uriBuilder.build();
            HttpPut httpPut = new HttpPut(uri);

            String json = JSON.toJSONString(object);
            httpPut.setEntity(new StringEntity(json, "UTF-8"));

            headers.forEach((k,v) -> {
                httpPut.setHeader(k,v);
            });

            httpPut.addHeader("Content-type","application/json;");

            return doRequest(httpClient,httpPut,uri);
        }catch (URISyntaxException e) {
            logger.error("url解析失败: " + e);
            return null;
        }
    }

    public static FetchData put(String scheme, String host, String path, Map<String, String>params, Map<String, String>headers){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPut httpPut = new HttpPut(uri);

            httpPut.setEntity(new UrlEncodedFormEntity(generateNameValiePair(params), "UTF-8"));

            headers.forEach((k,v) -> {
                httpPut.setHeader(k,v);
            });

            return doRequest(httpClient,httpPut,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }catch (UnsupportedEncodingException e){
            logger.error("参数编码失败: " + e);
            return null;
        }
    }

    public static FetchData put(String scheme, String host, String path, Map<String, String>params){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPut httpPut = new HttpPut(uri);

            httpPut.setEntity(new UrlEncodedFormEntity(generateNameValiePair(params), "UTF-8"));

            return doRequest(httpClient,httpPut,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }catch (UnsupportedEncodingException e){
            logger.error("参数编码失败: " + e);
            return null;
        }
    }

    public static FetchData put(String scheme, String host, String path){
        if (StringUtils.isEmpty(scheme) || StringUtils.isEmpty(host)){
            logger.warn("请求访问的url为空");
            return null;
        }

        CloseableHttpClient httpClient = generateHttpClientByScheme(scheme);
        if (httpClient == null){
            logger.error("scheme参数异常: 不为http或https");
            return null;
        }

        try {
            URIBuilder uriBuilder = generateUri(scheme, host, path);
            URI uri = uriBuilder.build();
            HttpPut httpPut = new HttpPut(uri);
            return doRequest(httpClient,httpPut,uri);
        }catch (URISyntaxException e){
            logger.error("url解析失败: " + e);
            return null;
        }

    }

    private static URIBuilder generateUri(String scheme, String host, String path){
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(scheme)
                .setHost(host);
        if (StringUtils.isNotEmpty(path)){
            uriBuilder.setPath(path);
        }
        return uriBuilder;
    }

    private static URIBuilder generateUriWithParams(String scheme, String host, String path, Map<String, String> params){
        URIBuilder uriBuilder = generateUri(scheme, host, path);
        if (params != null) {
            params.forEach((k, v) -> {
                uriBuilder.addParameter(k, v);
            });
        }
        try {
        }catch (Exception e){

        }
        return uriBuilder;
    }

    private static CloseableHttpClient generateHttpClientByScheme(String schema){
        if (schema.equals("https")){
            return httpsClient;
        }
        return httpClient;
    }

    private static List<NameValuePair> generateNameValiePair(Map<String ,String> params){
        List<NameValuePair> nvps = new ArrayList<>(params.size());
        params.forEach((k,v) -> {
            nvps.add(new BasicNameValuePair(k,v));
        });
        return nvps;
    }

    private static FetchData doRequest(CloseableHttpClient httpClient, HttpRequestBase httpRequestBase, URI uri){
        try (CloseableHttpResponse response = httpClient.execute(httpRequestBase)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), "UTF-8");
            FetchData fetchData = new FetchData(statusCode, content, uri.toString());
            response.close();
            return fetchData;
        } catch (Exception e){
            logger.error("请求失败: " + e);
            return null;
        }
    }
}
