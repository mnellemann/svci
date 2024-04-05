package biz.nellemann.svci;

import org.mockserver.integration.ClientAndServer
import org.mockserver.logging.MockServerLogger
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import org.mockserver.socket.PortFactory
import org.mockserver.socket.tls.KeyStoreFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.net.ssl.HttpsURLConnection
import java.util.concurrent.TimeUnit

@Stepwise
class RestClientTest extends Specification {

    @Shared
    private static ClientAndServer mockServer;

    @Shared
    private RestClient serviceClient


    def setupSpec() {
        HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(new MockServerLogger()).sslContext().getSocketFactory());
        mockServer = ClientAndServer.startClientAndServer(PortFactory.findFreePort());
        serviceClient = new RestClient("localhost", "superuser", "password", mockServer.getPort(), true)
    }

    def cleanupSpec() {
        mockServer.stop()
    }

    def setup() {
        mockServer.reset()
    }


    def "Test POST Request"() {
        setup:
        def req = HttpRequest.request()
            .withMethod("POST")
            .withPath("/test/post")
        def res = HttpResponse.response()
            .withDelay(TimeUnit.SECONDS, 1)
            .withStatusCode(202)
            .withHeaders(
                new Header("Content-Type", "text/plain; charset=UTF-8"),
            )
            .withBody("Created, OK.", MediaType.TEXT_PLAIN)
        mockServer.when(req).respond(res)

        when:
        String response = serviceClient.postRequest("/test/post", null)

        then:
        response == "Created, OK."
    }


    def "Test SVC Login"() {
        setup:
        def responseFile = new File(getClass().getResource('/json/svc-auth-response.json').toURI())
        def req = HttpRequest.request()
            .withHeader("X-Auth-Username", "superuser")
            .withHeader("X-Auth-Password", "password")
            .withMethod("POST")
            .withPath("/rest/v1/auth")

        def res = HttpResponse.response()
            .withDelay(TimeUnit.SECONDS, 1)
            .withStatusCode(200)
            .withHeaders(
                new Header("Content-Type", "application/json"),
            )
            .withBody(responseFile.getText(), MediaType.APPLICATION_JSON)

        mockServer.when(req).respond(res)

        when:
        serviceClient.login()

        then:
        serviceClient.authToken == "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE2Njk2MjY3MTMsImV4cCI6MTY2OTYzMDMxMywianRpIjoiN2UxYWJiZmJmNzlkMWE3YTVlNGI1MjM1M2VlZmM0ZDkiLCJzdiI6eyJ1c2VyIjoic3VwZXJ1c2VyIn19.B8MVI5XvmKi-ONX1NTaDmcMEB6SVd93kfW8beKu3Mfl70tGwCotY5-lQ3R4sZWd4hiEqvsrrCm3o1afUGlCxJw"
    }



}


