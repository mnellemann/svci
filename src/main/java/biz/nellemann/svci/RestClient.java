package biz.nellemann.svci;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import biz.nellemann.svci.dto.json.AuthResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestClient {

    private final static Logger log = LoggerFactory.getLogger(RestClient.class);

    protected OkHttpClient httpClient;

    // OkHttpClient timeouts
    private final static int CONNECT_TIMEOUT = 30;
    private final static int WRITE_TIMEOUT = 30;
    private final static int READ_TIMEOUT = 180;

    protected String authToken;

    protected final static String protocol = "https";
    protected final Integer port;
    protected final String hostname;
    protected final String username;
    protected final String password;

    private final HashMap<String, Integer> urlErrorCounter = new HashMap<String, Integer>();


    public RestClient(String hostname, String username, String password, Integer port, Boolean trustAll) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;
        if (trustAll) {
            this.httpClient = getUnsafeOkHttpClient();
        } else {
            this.httpClient = getSafeOkHttpClient();
        }
    }


    /**
     * Logon to the SVC and get an authentication token for further requests.
     */
    public synchronized void login() {

        try {
            URL url = new URL(protocol, hostname, port, "/rest/v1/auth");
            log.info("Connecting to SVC - {} @ {}", username, url);

            Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Audit-Memento", "IBM Power HMC Insights")
                .addHeader("X-Auth-Username", username)
                .addHeader("X-Auth-Password", password)
                //.put(RequestBody.create(payload.toString(), MEDIA_TYPE_IBM_XML_LOGIN))
                //.post(RequestBody.create("", MediaType.get("text/plain")))
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

            String responseBody;
            try (Response response = httpClient.newCall(request).execute()) {
                responseBody = Objects.requireNonNull(response.body()).string();
                if (!response.isSuccessful()) {
                    log.warn("login() - Unexpected response: {}", response.code());
                    throw new IOException("Unexpected code: " + response);
                }
            }

            log.debug(responseBody);
            ObjectMapper objectMapper = new ObjectMapper();
            AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);

            authToken = authResponse.token;
            log.debug("logon() - auth token: {}", authToken);
            urlErrorCounter.clear();

        } catch (IOException e) {
            log.warn("logon() - error: {}", e.getMessage());
        }

    }


    public String postRequest(String urlPath) throws IOException {
        //URL absUrl = new URL(String.format("%s%s", baseUrl, urlPath));
        URL absUrl = new URL(protocol, hostname, port, urlPath);
        return postRequest(absUrl, null);
    }


    public String postRequest(String urlPath, String payload) throws IOException {
        //URL absUrl = new URL(String.format("%s%s", baseUrl, urlPath));
        URL absUrl = new URL(protocol, hostname, port, urlPath);
        return postRequest(absUrl, payload);
    }


    /**
     * Send a POST request with a payload (can be null) to the SVC
     * @param url
     * @param payload
     * @return
     * @throws IOException
     */
    public synchronized String postRequest(URL url, String payload) throws IOException {

        log.trace("postRequest() - URL: {}", url.toString());
        if(hasErrorForUrl(url.toString())) {
            log.debug("postRequest() - breaking due to error counter for url: {}", url);
            return null;
        }

        RequestBody requestBody;
        if(payload != null) {
            requestBody = RequestBody.create(payload, MediaType.get("application/json"));
        } else {
            requestBody = RequestBody.create("", null);
        }

        Request request = new Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Auth-Token", (authToken == null ? "" : authToken) )
            .post(requestBody).build();

        String responseBody;
        try (Response response = httpClient.newCall(request).execute()) {
            responseBody = Objects.requireNonNull(response.body()).string();

            if (!response.isSuccessful()) {
                if(response.code() == 401) {
                    log.warn("postRequest() - 401: login and retry.");
                    login();
                    return retryPostRequest(url, payload);
                }

                log.warn("{}: {} <= \"{}\" => {}", response.code(), url, payload, responseBody);
                logErrorForUrl(url.toString());

                log.error("postRequest() - Unexpected response: {}", response.code());
                throw new IOException("postRequest() - Unexpected response: " + response.code());
            }
        }

        return responseBody;
    }


    private String retryPostRequest(URL url, String payload) throws IOException {

        log.debug("retryPostRequest() - URL: {}", url.toString());

        RequestBody requestBody;
        if(payload != null) {
            requestBody = RequestBody.create(payload, MediaType.get("application/json"));
        } else {
            requestBody = RequestBody.create("", null);
        }

        Request request = new Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Auth-Token", (authToken == null ? "" : authToken) )
            .post(requestBody).build();

        String responseBody = null;
        try (Response response = httpClient.newCall(request).execute()) {
            if(response.isSuccessful()) {
                responseBody = response.body().string();
            }
        }
        return responseBody;
    }


    /**
     * Provide an unsafe (ignoring SSL problems) OkHttpClient
     *
     * @return OkHttpClient ignoring SSL/TLS errors
     */
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {  }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // Create a ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);

            return builder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get OkHttpClient with our preferred timeout values.
     * @return OkHttpClient
     */
    private static OkHttpClient getSafeOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        return builder.build();
    }


    private boolean hasErrorForUrl(String url) {
        if(urlErrorCounter.containsKey(url)) {
            int errors = urlErrorCounter.get(url);
            return errors > 2;
        }
        return false;
    }


    private void logErrorForUrl(String url) {
        if(urlErrorCounter.containsKey(url)) {
            int errors = urlErrorCounter.get(url);
            urlErrorCounter.put(url, ++errors);
        } else {
            urlErrorCounter.put(url, 1);
        }
    }


}
