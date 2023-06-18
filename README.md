# Calculator API
This service allows you to perform basic mathematical operations like ADD, SUBTRACT, MULTIPLY, and DIVIDE. 

Calculator API is a serverless, event-driven compute service based on AWS Lambda. This repository contains AWS lambda function and cloud resource provisioning (IOC) configuration.  

# Development

## Technology:

* AWS Lambda with Kotlin (JDK 17)
* Maven as build tool

## Setup instructions

```shell
# Install aws-cli
brew install awscli

# Configure aws-cli (you have to provide Access keys and region)
aws configure 

# Verify aws-cli configuration
aws sts get-caller-identity

# Install cdk 
npm install -g aws-cdk@latest

# Check cdk version
cdk --version

# Bootstrap an AWS account by using the AWS CDK
cdk bootstrap aws://{account-number}/{region}
```

## Deploy instructions

```shell
# go to application directory
cd aws-lambda/function/calculator-lambda
# build lambda function using maven
mvn clean install

# go to infrastructure directory
cd aws-lambda/infrastructure
# build infrastructure using maven
mvn clean install

# Constructs your CloudFormation template
cdk synth
# Deploy
cdk deploy
```

## Functionality
- Calculator api accepts mandatory json body having first_name, second_number and operation.
- Calculator api only perform operation for valid operations
- Second number can not be 0 in case of DIVIDE operation
- Calculator api will return 200 response code for success and 400 response code for invalid request

## Test Calculator api

* URL: https://msj76kui51.execute-api.eu-north-1.amazonaws.com/calculate
* Method: POST

### Request Body: 
```json
{
    "first_number": 100,
    "second_number": 10,
    "operation": "MULTIPLY" 
}
```

### Response Body: 
```json
{
    "result": 1000.0
}
```

### Example
Using curl command: 
```curl
curl --location 'https://msj76kui51.execute-api.eu-north-1.amazonaws.com/calculate' \
--header 'Content-Type: application/json' \
--data '{
    "first_number": 100,
    "second_number": 10,
    "operation": "MULTIPLY" 
}'
```

### Test Calculator api via IntelliJ locally

[Http request file](https://github.com/jainakash2108/aws-lambda/blob/main/request.http)
