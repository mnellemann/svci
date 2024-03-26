package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortStat {

    /**
     *
     */
    @JsonProperty("id")
    public String id;


    /**
     *
     */
    @JsonProperty("type")
    public String type;


    /**
     */
    @JsonProperty("hbt")
    public Long hbt;


    /**
     */
    @JsonProperty("hbr")
    public Long hbr;

    /**
     */
    @JsonProperty("het")
    public Long het;


    /**
     */
    @JsonProperty("her")
    public Long her;


    /**
     */
    @JsonProperty("cbt")
    public Long cbt;


    /**
     */
    @JsonProperty("cbr")
    public Long cbr;


    /**
     */
    @JsonProperty("cet")
    public Long cet;


    /**
     */
    @JsonProperty("cer")
    public Long cer;



    /**
     */
    @JsonProperty("lnbt")
    public Long lnbt;


    /**
     */
    @JsonProperty("lnbr")
    public Long lnbr;


    /**
     */
    @JsonProperty("lnet")
    public Long lnet;


    /**
     */
    @JsonProperty("lner")
    public Long lner;


    /**
     */
    @JsonProperty("rmbt")
    public Long rmbt;

    /**
     */
    @JsonProperty("rmbr")
    public Long rmbr;

    /**
     */
    @JsonProperty("rmet")
    public Long rmet;

    /**
     */
    @JsonProperty("rmer")
    public Long rmer;


    /*
        <port id="5"
              type="PCIe"
              type_id="1"
              wwpn="0x0000000000000000"
              fc_wwpn=""
              fcoe_wwpn=""
              sas_wwn=""
              iqn=""
              hbt="821305" hbr="188416" het="0" her="2267"
              cbt="192512" cbr="1545249" cet="2364" cer="0"
              lnbt="250174778332" lnbr="1108768621845" lnet="3142328837" lner="3138670341"
              rmbt="0" rmbr="0" rmet="0" rmer="0"
        />
    */


}
