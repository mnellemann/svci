package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeStatCollection {

    @JsonProperty("schemaLocation")
    @JacksonXmlProperty(isAttribute = true)
    public String schemaLocation;

    @JsonProperty("scope")
    public String scope;

    @JsonProperty("id")
    public String id;

    @JsonProperty("cluster")
    public String cluster;

    @JsonProperty("cluster_id")
    public String clusterId;

    @JsonProperty("node_id")
    public String nodeId;

    @JsonProperty("sizeUnits")
    public String siteUnits;

    @JsonProperty("timeUnits")
    public String timeUnits;

    @JsonProperty("contains")
    public String contains;

    @JsonProperty("timestamp")
    public String timestamp;

    @JsonProperty("timezone")
    public String timezone;

    @JsonProperty("timestamp_utc")
    public String timestampUtc;


    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("node")
    public List<NodeStat> nodeStats = new ArrayList<>();


    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("port")
    public List<PortStat> portStats = new ArrayList<>();


}
