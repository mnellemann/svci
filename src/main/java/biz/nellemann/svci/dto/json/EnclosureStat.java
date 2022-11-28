package biz.nellemann.svci.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnclosureStat {

    @JsonProperty("enclosure_id")
    public String enclosureId;

    @JsonProperty("stat_name")
    public String statName;

    @JsonProperty("stat_current")
    public Number statCurrent;

    @JsonProperty("stat_peak")
    public Number statPeak;

    @JsonProperty("stat_peak_time")
    public Number statPeakTime;

    /*
    "enclosure_id": "1",
    "stat_name": "power_w",
    "stat_current": "332",
    "stat_peak": "333",
    "stat_peak_time": "221126132328"
     */
}
