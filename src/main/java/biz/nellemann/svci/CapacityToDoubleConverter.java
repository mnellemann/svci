package biz.nellemann.svci;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jackson Converter
 * Converts a "storage capacity" presented as a String with a unit (eg. MB) to a Double value in TB.
 */
public class CapacityToDoubleConverter extends StdConverter<String, Double> {

    private final static Logger log = LoggerFactory.getLogger(CapacityToDoubleConverter.class);

    final private Pattern p = Pattern.compile("(^\\d*\\.?\\d*)\\s?(\\D{2})$");


    @Override
    public Double convert(String value) {
        Matcher m = p.matcher(value);
        if(!m.matches()) {
            return null;
        }

        double input = Double.parseDouble(m.group(1));
        String unit = m.group(2);
        log.debug("Input: {} {}", input, unit);

        double output = input;
        if(unit.equals("PB")) {
            output = input * 1000;
        } else if(unit.equals("TB")) {
            output = input;
        } else if(unit.equals("GB")) {
            output = input / 1000;
        } else if(unit.equals("MB")) {
            output = input / 1_000_000;
        } else {
            log.warn("convert() - Unit {} not supported.", unit);
        }

        log.debug("Output: {} TB", output);
        return output;
    }

}
