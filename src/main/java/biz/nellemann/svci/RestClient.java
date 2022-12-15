package biz.nellemann.svci;

import biz.nellemann.svci.dto.json.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RestClient {

    private final static Logger log = LoggerFactory.getLogger(RestClient.class);

    protected OkHttpClient httpClient;

    // OkHttpClient timeouts
    private final static int CONNECT_TIMEOUT = 30;
    private final static int WRITE_TIMEOUT = 30;
    private final static int READ_TIMEOUT = 180;

    protected String authToken;
    protected final String baseUrl;
    protected final String username;
    protected final String password;


    public RestClient(String baseUrl, String username, String password, Boolean trustAll) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
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

        log.info("Connecting to SVC - {} @ {}", username, baseUrl);

        try {
            URL url = new URL(String.format("%s/rest/v1/auth", baseUrl));
            Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Audit-Memento", "IBM Power HMC Insights")
                .addHeader("X-Auth-Username", username)
                .addHeader("X-Auth-Password", password)
                //.put(RequestBody.create(payload.toString(), MEDIA_TYPE_IBM_XML_LOGIN))
                .post(RequestBody.create("", MediaType.get("text/plain")))
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

        } catch (Exception e) {
            log.warn("logon() - error: {}", e.getMessage());
        }

    }


    public String postRequest(String urlPath) throws IOException {
        URL absUrl = new URL(String.format("%s%s", baseUrl, urlPath));
        return postRequest(absUrl, null);
    }


    public String postRequest(String urlPath, String payload) throws IOException {
        URL absUrl = new URL(String.format("%s%s", baseUrl, urlPath));
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
                    log.warn("postRequest() - 401 - login and retry.");

                    // Let's login again and retry
                    login();
                    return retryPostRequest(url, payload);
                }
                log.warn(responseBody);
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


}
