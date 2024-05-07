package biz.nellemann.svci

import biz.nellemann.svci.dto.json.EnclosureStat
import biz.nellemann.svci.dto.json.System
import biz.nellemann.svci.dto.json.NodeStat
import biz.nellemann.svci.dto.xml.DriveStatCollection
import biz.nellemann.svci.dto.xml.MDiskStatCollection
import biz.nellemann.svci.dto.xml.NodeStatCollection
import biz.nellemann.svci.dto.xml.VDiskStatCollection
import biz.nellemann.svci.dto.xml.VolumeGroupStatCollection
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

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


    void "io-stats for drive (Nd_stats)"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/Nd_stats_78F1BZZ-2_240325_190303.xml').toURI())
        DriveStatCollection diskStatCollection = xmlMapper.readerFor(DriveStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(diskStatCollection.timestamp).toEpochMilli() == 1711389783000L
        diskStatCollection.driveStats.size() == 8
        diskStatCollection.driveStats.get(0).urq == 85469020667L
    }



    void "io-stats for volume-group (Ng_stats)"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/Ng_stats_78F1BZZ-2_240325_190303.xml').toURI())
        VolumeGroupStatCollection volumeGroupStatCollection = xmlMapper.readerFor(VolumeGroupStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(volumeGroupStatCollection.timestamp).toEpochMilli() == 1711389783000L
        volumeGroupStatCollection.volumeGroupStats.size() == 2
        volumeGroupStatCollection.volumeGroupStats.get(0).name == "SafeguardedCopy_VolumeGroup"
    }


    void "io-stats for mdisk (Nm_stats)"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/Nm_stats_78F1BZZ-2_240325_190303.xml').toURI())
        MDiskStatCollection mDiskStatCollection = xmlMapper.readerFor(MDiskStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(mDiskStatCollection.timestamp).toEpochMilli() == 1711389783000L
        mDiskStatCollection.mDiskStats.size() == 1
        mDiskStatCollection.mDiskStats.get(0).urq == 4148033741L
    }


    void "io-stats for node (Nn_stats)"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/Nn_stats_78F1BZZ-2_240325_190303.xml').toURI())
        NodeStatCollection nodeStatCollection = xmlMapper.readerFor(NodeStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(nodeStatCollection.timestamp).toEpochMilli() == 1711389783000L
        nodeStatCollection.nodeStats.size() == 3
        nodeStatCollection.nodeStats.get(0).lrb == 1016167148042L
        nodeStatCollection.nodeStats.get(0).lwb == 93481412171L
    }


    void "io-stats for port (Nn_stats)"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/Nn_stats_78F1BZZ-2_240325_190303.xml').toURI())
        NodeStatCollection nodeStatCollection = xmlMapper.readerFor(NodeStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(nodeStatCollection.timestamp).toEpochMilli() == 1711389783000L
        nodeStatCollection.portStats.size() == 9
        nodeStatCollection.portStats.get(0).cbr == 0L
        nodeStatCollection.portStats.get(0).cbt == 0L
    }


    void "io-stats for vdisk (Nv_stats)"() {

        when:
        Path testConfigurationFile = Paths.get(getClass().getResource('/xml/v8.6/Nv_stats_78F1BZZ-2_240325_190303.xml').toURI())
        VDiskStatCollection vDiskStatCollection = xmlMapper.readerFor(VDiskStatCollection.class).readValue(testConfigurationFile.toFile())

        then:
        Utils.parseDateTime(vDiskStatCollection.timestamp).toEpochMilli() == 1711389783000L
        vDiskStatCollection.vDiskStats.size() == 78
        vDiskStatCollection.vDiskStats.get(0).id == "Compressed_Dedup_Volumes0"
        vDiskStatCollection.vDiskStats.get(0).idx == "12"
    }


}
