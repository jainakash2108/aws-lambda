package com.math

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CalculatorLambdaTest {
    @Mock
    private val mockContext: Context? = null
    private var calculatorLambda: CalculatorLambda? = null

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        calculatorLambda = CalculatorLambda()
    }

    @Test
    fun should_return_200_response_code_with_result_for_addition_operation() {
        val request = APIGatewayProxyRequestEvent()
            .withBody("{ \"first_number\": 2, \"second_number\": 3, \"operation\": \"ADD\" }")
        val response = calculatorLambda!!.handleRequest(request, mockContext!!)
        Assertions.assertEquals(200, response.statusCode)
        Assertions.assertEquals("{ \"result\": 5.0 }", response.body)
    }

    @Test
    fun should_return_400_response_code_with_message_for_invalid_json_request() {
        val request = APIGatewayProxyRequestEvent()
            .withBody("{ \"first_number\": 2, \"second_number\": 3 }") // Missing operation
        val response = calculatorLambda!!.handleRequest(request, mockContext!!)
        Assertions.assertEquals(400, response.statusCode)
        Assertions.assertEquals("{ \"message\": \"Invalid json body\" }", response.body)
    }

    @Test
    fun should_return_400_response_code_with_message_for_invalid_operation() {
        val request = APIGatewayProxyRequestEvent()
            .withBody("{ \"first_number\": 2, \"second_number\": 3, \"operation\": \"INVALID\" }")
        val response = calculatorLambda!!.handleRequest(request, mockContext!!)
        Assertions.assertEquals(400, response.statusCode)
        Assertions.assertEquals("{ \"message\": \"Invalid operation\" }", response.body)
    }

    @Test
    fun should_return_400_response_code_with_message_for_invalid_numbers() {
        val request = APIGatewayProxyRequestEvent()
            .withBody("{ \"first_number\": 5, \"second_number\": 0, \"operation\": \"DIVIDE\" }")
        val response = calculatorLambda!!.handleRequest(request, mockContext!!)
        Assertions.assertEquals(400, response.statusCode)
        Assertions.assertEquals("{ \"message\": \"Invalid numbers. Second number should not be 0\" }", response.body)
    }
}
