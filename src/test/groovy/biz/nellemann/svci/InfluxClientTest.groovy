package biz.nellemann.svci

import biz.nellemann.svci.dto.toml.InfluxConfiguration
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class InfluxClientTest extends Specification {

    InfluxClient influxClient

    def setup() {
        influxClient = new InfluxClient(new InfluxConfiguration("http://localhost:8086", "root", "", "svci"))
        influxClient.login()
    }

    def cleanup() {
        influxClient.logoff()
    }


}
