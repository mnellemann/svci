package biz.nellemann.svci

import biz.nellemann.svci.dto.json.EnclosureStat
import biz.nellemann.svci.dto.json.System
import biz.nellemann.svci.dto.json.NodeStat
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class DeserializationTest extends Specification {


    ObjectMapper mapper

    def setup() {
        mapper = new ObjectMapper();
    }

    def cleanup() {
    }


    void "lssystem"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/lssystem.json').toURI())
        System system = mapper.readerFor(System.class).readValue(testConfigurationFile.toFile())

        then:
        system.name == "V7000_A2U12"
        system.location == "local"
        system.codeLevel == "8.4.2.0 (build 154.20.2109031944000)"
        system.productName == "IBM Storwize V7000"
    }


    void "lsnodestat"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/lsnodestats.json').toURI())
        List<NodeStat> nodeStats = Arrays.asList(mapper.readerFor(NodeStat[].class).readValue(testConfigurationFile.toFile()))

        then:
        nodeStats.size() == 92
        nodeStats.get(0).nodeName == "node1"
        nodeStats.get(0).statName == "compression_cpu_pc"
        nodeStats.get(0).statCurrent == 0
    }


    void "lsenclosurestats"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/lsenclosurestats.json').toURI())
        List<EnclosureStat> enclosureStats = Arrays.asList(mapper.readerFor(EnclosureStat[].class).readValue(testConfigurationFile.toFile()))

        then:
        enclosureStats.size() == 6
        enclosureStats.get(0).enclosureId == "1"
        enclosureStats.get(0).statName == "power_w"
        enclosureStats.get(0).statCurrent == 332
        enclosureStats.get(0).statPeak == 333
        enclosureStats.get(0).statPeakTime == 221126132328
    }

}
