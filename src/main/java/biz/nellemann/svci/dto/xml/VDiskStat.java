package biz.nellemann.svci.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * See {@link https://www.ibm.com/docs/en/sanvolumecontroller/8.6.x?topic=troubleshooting-starting-statistics-collection}
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class VDiskStat {

    /**
     * Name of vDisk
     */
    @JsonProperty("id")
    public String id;

    /**
     * Number (ID) of vDisk
     */
    @JsonProperty("idx")
    public String idx;

    /**
     * Read operations
     */
    @JsonProperty("ro")
    public Long ro;

    /**
     * Write operations
     */
    @JsonProperty("wo")
    public Long wo;

    /**
     * Read blocks (512 bytes)
     */
    @JsonProperty("rb")
    public Long rb;

    /**
     * Write blocks (512 bytes)
     */
    @JsonProperty("wb")
    public Long wb;

    /**
     * Cumulative Read response time in milliseconds
     */
    @JsonProperty("rl")
    public Long rl;

    /**
     * Cumulative write response time in milliseconds
     */
    @JsonProperty("wl")
    public Long wl;

    /**
     * Worst Read response time in microseconds since last statistics collection
     */
    @JsonProperty("rlw")
    public Long rlw;

    /**
     * Worst Write response time in microseconds since last statistics collection
     */
    @JsonProperty("wlw")
    public Long wlw;

    /**
     * Cumulative transfer response time in microseconds
     */
    @JsonProperty("xl")
    public Long xl;

    /*
    <vdsk idx="31"
          ctps="0" ctrhs="0" ctrhps="0" ctds="0"
          ctwfts="0" ctwwts="0" ctwfws="0" ctwhs="0"
          cv="0" cm="0" ctws="0" ctrs="0"
          ctr="0" ctw="0" ctp="0" ctrh="0"
          ctrhp="0" ctd="0" ctwft="0" ctwwt="0"
          ctwfw="0" ctwfwsh="0" ctwfwshs="0" ctwh="0"
          varp="0" vwrp="0" gwot="0" gwo="0" gws="0" gwl="0"

          id="HA_Volume2"
          ro="0" wo="0" wou="0" rb="0" wb="0"
          rl="0" wl="0" rlw="0" wlw="0" xl="0"
          wxl="0" rxl="0" oro="0" owo="0"
          orl="0" owl="0" oiowp="0"
          uo="0" ub="0" uou="0" ul="0" ulw="0">
        <fc wlag="0" wlcn="0" wlmx="0" rlag="0" rlcn="0" rlmx="0" bcag="0" bccn="0" bcmx="0" clag="0" clcn="0" clmx="0"
            rwag="0" rwcn="0" rwmx="0" twag="0" twcn="0" twmx="0" trag="0" trcn="0" trmx="0" bwag="0" bwcn="0" bwmx="0"
            brag="0"
            brcn="0" brmx="0" hwsg="0" hwdg="0" srcp="0" twbl="0" trbl="0" bwbl="0" brbl="0"/>
        <ca rh="0" d="0" ft="0" wt="0"
            fw="0" wh="0" v="0" m="0"
            ri="0" wi="0" r="0" dav="0" dcn="0" sav="0" scn="0" teav="0"
            tsav="0" tav="0" entav="0" entcn="0" entmx="0" entmn="0" pp="0"/>
        <cl bup="0" bdn="0"/>
        <cpy idx="0">
            <ca p="0" rh="0" ph="0" d="0"
                ft="0" wt="0" fw="0" wh="0"
                v="0" m="0" pm="0" ri="0"
                wi="0" r="0" dav="0" dcn="0"
                sav="0" scn="0" pav="0" pcn="0"
                teav="0" tsav="0" tav="0" entav="0" entcn="0" entmx="0" entmn="0"
                pp="0"/>
        </cpy>
    </vdsk>

     */
}
