package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeGroupStat {

    /*
        idx="1" name="HA_Volume Group"
        rarp="0" rwrp="0" rnrw="0" rnrb="0"
        rhalwl="0" rhalwc="0" rhalww="0" rharwl="0" rharwc="0"
     */


    /**
     *
     */
    @JsonProperty("name")
    public String name;


    /**
     *
     */
    @JsonProperty("idx")
    public String idx;


    /**
     *
     */
    @JsonProperty("rarp")
    public Long rarp;


    /**
     *
     */
    @JsonProperty("rwrp")
    public Long rwrp;


    /**
     *
     */
    @JsonProperty("rnrw")
    public Long rnrw;


    /**
     *
     */
    @JsonProperty("rnrb")
    public Long rnrb;


    /**
     *
     */
    @JsonProperty("rhalwl")
    public Long rhalwl;


    /**
     *
     */
    @JsonProperty("rhalwc")
    public Long rhalwc;


    /**
     *
     */
    @JsonProperty("rhalww")
    public Long rhalww;


    /**
     *
     */
    @JsonProperty("rharwl")
    public Long rharwl;


    /**
     *
     */
    @JsonProperty("rharwc")
    public Long rharwc;


}
