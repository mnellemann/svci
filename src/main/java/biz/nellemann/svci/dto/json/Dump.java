package biz.nellemann.svci.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dump {

    @JsonProperty("id")
    public Integer id;

    @JsonProperty("filename")
    public String filename;

}
