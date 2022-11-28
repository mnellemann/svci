package biz.nellemann.svci.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeStat {

    @JsonProperty("node_id")
    public String nodeId;

    @JsonProperty("node_name")
    public String nodeName;

    @JsonProperty("stat_name")
    public String statName;

    @JsonProperty("stat_current")
    public Number statCurrent;

    @JsonProperty("stat_peak")
    public Number statPeak;

    @JsonProperty("stat_peak_time")
    public Number statPeakTime;

    /*
    {
    "node_id": "2",
    "node_name": "node2",
    "stat_name": "cloud_down_ms",
    "stat_current": "0",
    "stat_peak": "0",
    "stat_peak_time": "221126132038"
    },
    */
}
