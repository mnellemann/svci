package biz.nellemann.svci.dto.xml;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiskStat {

    /*
    idx="0" id="" ro="293454155" wo="1355883" rb="150449489584" wb="84301344" re="86109200" we="70482" rq="86641421" wq="75326"
          ure="86172697868" uwe="70534802" urq="86705311130" uwq="75381664"
          pre="70" pwe="338" pro="77" pwo="343"
     */

    @JsonProperty("id")
    public String id;

    @JsonProperty("idx")
    public String idx;

    @JsonProperty("ro")
    public Long ro;

    @JsonProperty("wo")
    public Long wo;

    @JsonProperty("rb")
    public Long rb;

    @JsonProperty("wb")
    public Long wb;

    @JsonProperty("re")
    public Long re;

    @JsonProperty("we")
    public Long we;

    @JsonProperty("rq")
    public Long rq;

    @JsonProperty("wq")
    public Long wq;

    @JsonProperty("ure")
    public Long ure;

    @JsonProperty("uwe")
    public Long uwe;

    @JsonProperty("urq")
    public Long urq;

    @JsonProperty("uwq")
    public Long uwq;

    @JsonProperty("pre")
    public Long pre;

    @JsonProperty("pwe")
    public Long pwe;

    @JsonProperty("pro")
    public Long pro;

    @JsonProperty("pwo")
    public Long pwo;

}
