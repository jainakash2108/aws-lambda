package com.math;

import software.amazon.awscdk.App;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApiProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

public class CalculatorLambdaStack extends Stack {
    public CalculatorLambdaStack(final App parent, final String id) {
        this(parent, id, null);
    }

    public CalculatorLambdaStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        List<String> calculateLambdaPackagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",
                "cd calculator-lambda " +
                        "&& mvn clean install " +
                        "&& cp /asset-input/calculator-lambda/target/calculator-lambda.jar /asset-output/"
        );

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(calculateLambdaPackagingInstructions)
                .image(Runtime.JAVA_17.getBundlingImage())
                .volumes(singletonList(
                        // Mount local .m2 repo to avoid download all the dependencies again inside the container
                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);

        Function calculateLambda = new Function(this, "calculator-lambda", FunctionProps.builder()
                .runtime(Runtime.JAVA_17)
                .code(Code.fromAsset("../function/", AssetOptions.builder()
                        .bundling(builderOptions
                                .command(calculateLambdaPackagingInstructions)
                                .build())
                        .build()))
                .handler("com.math.CalculatorLambda")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .logRetention(RetentionDays.ONE_WEEK)
                .build());

        HttpApi httpApi = new HttpApi(this, "calculator-lambda-api", HttpApiProps.builder()
                .apiName("calculator-lambda-api")
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/calculate")
                .methods(singletonList(HttpMethod.POST))
                .integration(new HttpLambdaIntegration("calculate-lambda", calculateLambda, HttpLambdaIntegrationProps.builder()
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());

        new CfnOutput(this, "HttApi", CfnOutputProps.builder()
                .description("Url for Http Api")
                .value(httpApi.getApiEndpoint())
                .build());
    }
}