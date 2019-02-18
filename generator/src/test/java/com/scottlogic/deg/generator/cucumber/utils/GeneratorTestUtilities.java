package com.scottlogic.deg.generator.cucumber.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scottlogic.deg.generator.cucumber.steps.DateObject;
import com.scottlogic.deg.generator.cucumber.steps.DateValueStep;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;
import org.junit.Assert;

import java.io.IOException;
import java.time.LocalDateTime;

public class GeneratorTestUtilities {
    private static final ObjectMapper mapper = createMapper();

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        return mapper;
    }

    public static Object parseInput(String input) throws JsonParseException, InvalidProfileException {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.substring(1, input.length() - 1);
        } else if (input.matches(DateValueStep.DATE_REGEX)) {
            return new DateObject(input);
        } else if (input.equals("null")) {
            return null;
        } else if (input.matches("[+-]?(\\d+(\\.\\d+)?)")) {
            return parseNumber(input);
        } else if (input.equals("true") || input.equals("false")){
            return input.equals("true");
        }

        throw new InvalidProfileException(String.format("Unable to determine correct type for `%s`.\nEnsure strings are wrapped in double-quotes.", input));
    }

    public static Object parseNumber(String input) throws JsonParseException {
        try {
            return mapper.readerFor(Number.class).readValue(input);
        }
        catch (JsonParseException e){
            throw e;
        }
        catch (IOException e) {
            Assert.fail("Unexpected IO exception " + e.toString());
            return "<unexpected IO exception>";
        }
    }

    public static Object parseExpected(String input) throws JsonParseException, InvalidProfileException {
        if (input.matches(DateValueStep.DATE_REGEX)) {
            return LocalDateTime.parse(input);
        }
        return parseInput(input);
    }
}
