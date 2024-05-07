package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeStat {


    /**
     * Indicates the name of the node.
     */
    @JsonProperty("id")
    public String id;


    /**
     * Indicates the unique identifier for the node.
     */
    @JsonProperty("node_id")
    public String nodeId;


    /**
     * Indicates the name of the cluster.
     */
    @JsonProperty("cluster")
    public String cluster;


    /**
     * Indicates the identifier of the cluster.
     */
    @JsonProperty("cluster_id")
    public String clusterId;


    /**
     * Indicates the number of messages or bulk data received.
     */
    @JsonProperty("ro")
    public Long ro;


    /**
     * Indicates the number of messages or bulk data sent.
     */
    @JsonProperty("wo")
    public Long wo;


    /**
     * Indicates the number of physical bytes received from the other node.
     */
    @JsonProperty("rb")
    public Long rb;


    /**
     * Indicates the number of physical bytes sent to the other node.
     */
    @JsonProperty("wb")
    public Long wb;


    /**
     * Indicates the number of logical bytes received from the other node.
     */
    @JsonProperty("lrb")
    public Long lrb;


    /**
     * Indicates the number of logical bytes sent to the other node.
     */
    @JsonProperty("lwb")
    public Long lwb;


    /**
     * Indicates the accumulated receive latency, excluding inbound queue time.
     * This statistic is the latency that is experienced by the node communication
     * layer from the time that an I/O is queued to cache until the time that the
     * cache gives completion for it.
     */
    @JsonProperty("re")
    public Long re;


    /**
     * Indicates the accumulated send latency, excluding outbound queue time.
     * This statistic is the time from when the node communication layer issues a message out onto the
     * Fibre Channel until the node communication layer receives notification that the message arrived.
     */
    @JsonProperty("we")
    public Long we;


    /**
     * Indicates the accumulated receive latency, including inbound queue time. This statistic is the
     * latency from the time that a command arrives at the node communication layer to the time that
     * the cache completes the command.
     */
    @JsonProperty("rq")
    public Long rq;


    /**
     * Indicates the accumulated send latency, including outbound queue time. This statistic includes the
     * entire time that data is sent. This time includes the time from when the node communication layer
     * receives a message and waits for resources, the time to send the message to the remote node, and the
     * time that is taken for the remote node to respond.
     */
    @JsonProperty("wq")
    public Long wq;

     /*
    node id="node1" cluster="FS5200-4" node_id="0x0000000000000001" cluster_id="0x00000204a04055f4"
          ro="1005485456" wo="1006267807" rb="1154101839590" lrb="1016167148042"
          wb="276382575739" lwb="93481412171" re="27676" we="39190318"
          rq="2944961" wq="39344872"/>
     */
}
