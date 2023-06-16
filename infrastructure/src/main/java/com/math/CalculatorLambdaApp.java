package com.math;

import software.amazon.awscdk.App;

public class CalculatorLambdaApp {
    public static void main(final String[] args) {
        App app = new App();

        new CalculatorLambdaStack(app, "CalculatorLambdaApiPackagingStack");

        app.synth();
    }
}
