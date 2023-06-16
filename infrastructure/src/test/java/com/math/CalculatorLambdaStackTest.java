package com.math;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorLambdaStackTest {
    private final static ObjectMapper JSON = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    @Test
    public void testStack() throws IOException {
        App app = new App();
        CalculatorLambdaStack stack = new CalculatorLambdaStack(app, "test");

        JsonNode actual = JSON.valueToTree(app.synth().getStackArtifact(stack.getArtifactId()).getTemplate());

        assertThat(actual.toString())
                .contains("AWS::ApiGatewayV2::Api")
                .contains("AWS::Lambda::Function");
    }
}