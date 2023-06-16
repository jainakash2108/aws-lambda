package com.math

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

data class CalculatorRequest(
    @JsonProperty("first_number")
    val firstNumber: Double,
    @JsonProperty("second_number")
    val secondNumber: Double,
    @JsonProperty("operation")
    val operation: String,
)

enum class Operation {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
}

class CalculatorLambda : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(
        requestEvent: APIGatewayProxyRequestEvent,
        context: Context,
    ): APIGatewayProxyResponseEvent {
        val response = APIGatewayProxyResponseEvent().withHeaders(mapOf("Content-Type" to "application/json"))

        // Parse the JSON request body using Jackson
        val calculatorRequest: CalculatorRequest = try {
            ObjectMapper().readValue(requestEvent.body, CalculatorRequest::class.java)
        } catch (ex: Exception) {
            return response.withStatusCode(400)
                .withBody("{ \"message\": \"Invalid json body\" }")
        }

        // Validate operation
        val operation = try {
            Operation.valueOf(calculatorRequest.operation)
        } catch (ex: IllegalArgumentException) {
            return response.withStatusCode(400)
                .withBody("{ \"message\": \"Invalid operation\" }")
        }

        // Validate second number for divide operation
        if (operation == Operation.DIVIDE && calculatorRequest.secondNumber == 0.0) {
            return response.withStatusCode(400)
                .withBody("{ \"message\": \"Invalid numbers. Second number should not be 0\" }")
        }

        val result: Double = when (operation) {
            Operation.ADD -> calculatorRequest.firstNumber + calculatorRequest.secondNumber
            Operation.SUBTRACT -> calculatorRequest.firstNumber - calculatorRequest.secondNumber
            Operation.MULTIPLY -> calculatorRequest.firstNumber * calculatorRequest.secondNumber
            Operation.DIVIDE -> calculatorRequest.firstNumber / calculatorRequest.secondNumber
        }

        return response.withStatusCode(200).withBody("{ \"result\": $result }")
    }
}
