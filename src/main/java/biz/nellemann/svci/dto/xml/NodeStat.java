package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeStat {


    /**
     *
     */
    @JsonProperty("id")
    public String id;


    /**
     *
     */
    @JsonProperty("node_id")
    public String nodeId;


    /**
     *
     */
    @JsonProperty("cluster")
    public String cluster;


    /**
     *
     */
    @JsonProperty("cluster_id")
    public String clusterId;


    /**
     */
    @JsonProperty("ro")
    public Long ro;


    /**
     */
    @JsonProperty("wo")
    public Long wo;


    /**
     */
    @JsonProperty("rb")
    public Long rb;


    /**
     */
    @JsonProperty("wb")
    public Long wb;


    /**
     */
    @JsonProperty("lrb")
    public Long lrb;


    /**
     */
    @JsonProperty("lwb")
    public Long lwb;


    /**
     */
    @JsonProperty("re")
    public Long re;


    /**
     */
    @JsonProperty("we")
    public Long we;


    /**
     */
    @JsonProperty("rq")
    public Long rq;


    /**
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
