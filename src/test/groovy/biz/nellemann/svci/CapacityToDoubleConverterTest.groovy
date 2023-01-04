package biz.nellemann.svci

import spock.lang.Specification


class CapacityToDoubleConverterTest extends Specification {


    CapacityToDoubleConverter converter = new CapacityToDoubleConverter()


    def "convert from TB String to TB Double"() {
        when:
        def result = converter.convert("123.45TB")

        then:
        result == 123.45
    }


    def "convert from PB String to TB Double"() {
        when:
        def result = converter.convert("1024.0PB")

        then:
        result == 1024000.0
    }

    def "convert from PB (lowercase) String to TB Double"() {
        when:
        def result = converter.convert("1024.0pb")

        then:
        result == 1024000.0
    }

    def "convert from GB String to TB Double"() {
        when:
        def result = converter.convert("8192.0GB")

        then:
        result == 8.192
    }


    def "convert from GB String (with a space) to TB Double"() {
        when:
        def result = converter.convert("8192.0 GB")

        then:
        result == 8.192
    }


    def "convert from MB String to TB Double"() {
        when:
        def result = converter.convert("4096.0MB")

        then:
        result == 0.004096
    }

}
