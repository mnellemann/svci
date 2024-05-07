package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortStat {


    /**
     * Indicates the port identifier for the node.
     */
    @JsonProperty("id")
    public String id;


    /**
     * Type of port: FC, NVMe, PCIe, IPREP, iSCSI, 
     */
    @JsonProperty("type")
    public String type;


    /**
     * Indicates the worldwide port name for the node.
     */
    @JsonProperty("wwpn")
    public String wwpn;


    /**
     * Indicates the bytes transmitted to hosts.
     */
    @JsonProperty("hbt")
    public Long hbt;


    /**
     * Indicates the bytes received from hosts.
     */
    @JsonProperty("hbr")
    public Long hbr;


    /**
     * Indicates the commands that are initiated to hosts.
     * Note: The het metric is always 0.
     */
    @JsonProperty("het")
    public Long het;


    /**
     * Indicates the commands that are received from hosts.
     */
    @JsonProperty("her")
    public Long her;


    /**
     * Indicates the bytes transmitted to disk controllers.
     */
    @JsonProperty("cbt")
    public Long cbt;


    /**
     * Indicates the bytes received from controllers.
     */
    @JsonProperty("cbr")
    public Long cbr;


    /**
     * Indicates the commands that are initiated to disk controllers.
     */
    @JsonProperty("cet")
    public Long cet;


    /**
     * Indicates the commands that are received from disk controllers.
     * Note: The cer metric is always 0.
     */
    @JsonProperty("cer")
    public Long cer;



    /**
     * Indicates the bytes transmitted to other nodes in the same cluster.
     */
    @JsonProperty("lnbt")
    public Long lnbt;


    /**
     * Indicates the bytes received to other nodes in the same cluster.
     */
    @JsonProperty("lnbr")
    public Long lnbr;


    /**
     * Indicates the commands that are initiated to other nodes in the same cluster.
     */
    @JsonProperty("lnet")
    public Long lnet;


    /**
     * Indicates the commands that are received from other nodes in the same cluster.
     */
    @JsonProperty("lner")
    public Long lner;


    /**
     * Indicates the bytes transmitted to other nodes in the other clusters.
     */
    @JsonProperty("rmbt")
    public Long rmbt;


    /**
     * Indicates the bytes received to other nodes in the other clusters.
     */
    @JsonProperty("rmbr")
    public Long rmbr;


    /**
     * Indicates the commands that are initiated to other nodes in the other clusters.
     */
    @JsonProperty("rmet")
    public Long rmet;


    /**
     * Indicates the commands that are received from other nodes in the other clusters.
     */
    @JsonProperty("rmer")
    public Long rmer;


    /**
     * Indicates the bytes transmitted to other nodes in other clusters by the IP partnership driver.
     */
    @JsonProperty("iptx")
    public Long iptx;


    /**
     * Indicates the bytes received from other nodes in other clusters by the IP partnership driver.
     */
    @JsonProperty("iprx")
    public Long iprx;


    /**
     * Indicates the bytes retransmitted to other nodes in other clusters by the IP partnership driver.
     */
    @JsonProperty("ipre")
    public Long ipre;


    /**
     * Indicates the average size (in bytes) of data that is being transmitted by the IP partnership driver
     * since the last statistics collection period.
     */
    @JsonProperty("ipsz")
    public Long ipsz;


    /**
     * Indicates the average size (in bytes) of data that is being submitted to the IP partnership driver
     * since the last statistics collection period.
     */
    @JsonProperty("ipbz")
    public Long ipbz;


    /**
     * Indicates the average round-trip time in microseconds for the IP partnership link since the last statistics collection period.
     */
    @JsonProperty("iprt")
    public Long iprt;


    /**
     * Indicates the total bytes that are transmitted after any compression (if active) takes place.
     */
    @JsonProperty("iptc")
    public Long iptc;


    /**
     * Indicates the total bytes that are received before any decompression takes place.
     */
    @JsonProperty("iprc")
    public Long iprc;


}
