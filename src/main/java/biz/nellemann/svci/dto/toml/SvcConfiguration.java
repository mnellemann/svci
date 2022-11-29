package biz.nellemann.svci.dto.toml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SvcConfiguration {

    public String url;
    public String username;
    public String password;
    public Integer refresh = 30;
    public Boolean trust = true;

}
