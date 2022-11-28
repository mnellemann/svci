package biz.nellemann.svci.dto.toml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

    public InfluxConfiguration influx;
    public Map<String, SvcConfiguration> svc;

}
