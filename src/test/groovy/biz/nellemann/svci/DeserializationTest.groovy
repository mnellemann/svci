package biz.nellemann.svci

import biz.nellemann.svci.dto.json.EnclosureStat
import biz.nellemann.svci.dto.json.System
import biz.nellemann.svci.dto.json.NodeStat
import biz.nellemann.svci.dto.xml.DiskStat
import biz.nellemann.svci.dto.xml.DiskStatCollection
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

class DeserializationTest extends Specification {


    ObjectMapper jsonMapper
    ObjectMapper xmlMapper

    def setup() {
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();
    }

    def cleanup() {
    }


    void "lssystem v8_4"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/json/v8.4/lssystem.json').toURI())
        System system = jsonMapper.readerFor(System.class).readValue(testConfigurationFile.toFile())

        then:
        system.name == "V7000_A2U12"
        system.location == "local"
        system.codeLevel == "8.4.2.0 (build 154.20.2109031944000)"
        system.productName == "IBM Storwize V7000"
        system.totalUsedTB == 2.6
        system.totalFreeTB == 58.0
    }


    void "lsnodestat v8_4"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/json/v8.4/lsnodestats.json').toURI())
        List<NodeStat> nodeStats = Arrays.asList(jsonMapper.readerFor(NodeStat[].class).readValue(testConfigurationFile.toFile()))

        then:
        nodeStats.size() == 92
        nodeStats.get(0).nodeName == "node1"
        nodeStats.get(0).statName == "compression_cpu_pc"
        nodeStats.get(0).statCurrent == 0
    }


    void "lsnodestat v8_5"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/json/v8.5/lsnodestats_8.5.2.2.json').toURI())
        List<NodeStat> nodeStats = Arrays.asList(jsonMapper.readerFor(NodeStat[].class).readValue(testConfigurationFile.toFile()))

        then:
        nodeStats.size() == 92
        nodeStats.get(3).nodeName == "node1"
        nodeStats.get(3).statName == "fc_io"
        nodeStats.get(3).statCurrent == 2115
    }


    void "lsenclosurestats v8_4"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/json/v8.4/lsenclosurestats.json').toURI())
        List<EnclosureStat> enclosureStats = Arrays.asList(jsonMapper.readerFor(EnclosureStat[].class).readValue(testConfigurationFile.toFile()))

        then:
        enclosureStats.size() == 6
        enclosureStats.get(0).enclosureId == "1"
        enclosureStats.get(0).statName == "power_w"
        enclosureStats.get(0).statCurrent == 332
        enclosureStats.get(0).statPeak == 333
        enclosureStats.get(0).statPeakTime == 221126132328
    }


    void "iostats v8_6_1"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/iostats_8.6.1.xml').toURI())
        DiskStatCollection diskStatCollection = xmlMapper.readerFor(DiskStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(diskStatCollection.timestampUtc).toEpochMilli() ==1710848238000L
        diskStatCollection.diskStatList.size() == 8
        diskStatCollection.diskStatList.get(0).urq == 86705311130L
    }


}
