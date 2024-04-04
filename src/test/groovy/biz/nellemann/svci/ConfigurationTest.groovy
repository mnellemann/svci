package biz.nellemann.svci

import biz.nellemann.svci.dto.toml.Configuration
import biz.nellemann.svci.dto.toml.SvcConfiguration
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths


class ConfigurationTest extends Specification {

    Path testConfigurationFile = Paths.get(getClass().getResource('/svci.toml').toURI())

    TomlMapper mapper

    def setup() {
        mapper = new TomlMapper();
    }

    def cleanup() {
    }


    void "test parsing of configuration file"() {

        when:
        Configuration conf = mapper.readerFor(Configuration.class).readValue(testConfigurationFile.toFile())

        println(conf.svc.entrySet().forEach((e) -> {
            println((String)e.key + " -> " + e);
            SvcConfiguration c = e.value;
            println(c.hostname);
        }));

        then:
        conf != null
    }


}
