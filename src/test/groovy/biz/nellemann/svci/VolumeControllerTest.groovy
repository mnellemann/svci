package biz.nellemann.svci

import org.mockserver.integration.ClientAndServer
import org.mockserver.logging.MockServerLogger
import org.mockserver.socket.PortFactory
import org.mockserver.socket.tls.KeyStoreFactory
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import javax.net.ssl.HttpsURLConnection

@Ignore
class VolumeControllerTest extends Specification {

    @Shared
    private static ClientAndServer mockServer;

    @Shared
    private RestClient serviceClient

    @Shared
    private VolumeController volumeController

    @Shared
    private File metricsFile

    def setupSpec() {
        HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(new MockServerLogger()).sslContext().getSocketFactory());
        mockServer = ClientAndServer.startClientAndServer(PortFactory.findFreePort());
        serviceClient = new RestClient(String.format("http://localhost:%d", mockServer.getPort()), "user", "password", false)
        MockResponses.prepareClientResponseForLogin(mockServer)
        //MockResponses.prepareClientResponseForManagementConsole(mockServer)
        //MockResponses.prepareClientResponseForManagedSystem(mockServer)
        //MockResponses.prepareClientResponseForVirtualIOServer(mockServer)
        //MockResponses.prepareClientResponseForLogicalPartition(mockServer)
        serviceClient.login()
        volumeController = new VolumeController(serviceClient, );
        volumeController.discover()
    }

    def cleanupSpec() {
        mockServer.stop()
    }

    def setup() {
    }

    def "test we got entry"() {

        expect:
        volumeController.entry.getName() == "Server-9009-42A-SN21F64EV"
    }

    void "test getDetails"() {

        when:
        volumeController.deserialize(metricsFile.getText('UTF-8'))
        List<Measurement> listOfMeasurements = volumeController.getDetails()

        then:
        listOfMeasurements.size() == 1
        listOfMeasurements.first().tags['servername'] == 'Server-9009-42A-SN21F64EV'
        listOfMeasurements.first().fields['utilizedProcUnits'] == 0.00458
        listOfMeasurements.first().fields['assignedMem'] == 40448.0
    }

    void "test getMemoryMetrics"() {

        when:
        volumeController.deserialize(metricsFile.getText('UTF-8'))
        List<Measurement> listOfMeasurements = volumeController.getMemoryMetrics()

        then:
        listOfMeasurements.size() == 1
        listOfMeasurements.first().fields['totalMem'] == 1048576.000
    }

    void "test getProcessorMetrics"() {

        when:
        volumeController.deserialize(metricsFile.getText('UTF-8'))
        List<Measurement> listOfMeasurements = volumeController.getProcessorMetrics()

        then:
        listOfMeasurements.size() == 1
        listOfMeasurements.first().fields['availableProcUnits'] == 4.65
    }

    void "test getSystemSharedProcessorPools"() {

        when:
        volumeController.deserialize(metricsFile.getText('UTF-8'))
        List<Measurement> listOfMeasurements = volumeController.getSharedProcessorPools()

        then:
        listOfMeasurements.size() == 4
        listOfMeasurements.first().fields['assignedProcUnits'] == 22.00013
    }

    void "test getPhysicalProcessorPool"() {
        when:
        volumeController.deserialize(metricsFile.getText('UTF-8'))
        List<Measurement> listOfMeasurements = volumeController.getPhysicalProcessorPool()

        then:
        listOfMeasurements.size() == 1
        listOfMeasurements.first().fields['assignedProcUnits'] == 22.0

    }

}
