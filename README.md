# springboot-kafka-integration
This is a home lab implementation, based on the concepts that I learned during these days. The primary goal was integrate java using springboot with Kafka and add unit/integration tests using JUnit5.

For this specific project, I'm just implementing the __kafka-event-producer__ so far. Basically this producer is a client application that publish (write) events to Kafka topic.

## Stack version

  - Zookeeper version: 3.4.9
  - Kafka version: 2.5.0 (Confluent 5.5.1)
  - SpringBoot version: 2.4.0
  - Java 11
  - Junit5

# Requirements

## Docker

Please export your environment before starting the stack:
```
export DOCKER_HOST_IP=127.0.0.1
```
(that's the default value and you actually don't need to do anything)

+ Run with:
 ```
 docker-compose -f docker-compose.yml up
 ```
 
 ## Run the project using the IDE 
 + Import this project as Maven into your IDE. (I'm using Spring Tool Suite 4.)
 + Look for the class OrderEventsProducerApplication and execute to start the SpringBoot application
 
Now you can see message bellow:
 
 ```
 2020-11-16 23:52:42.166  INFO 20384 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.6.0
2020-11-16 23:52:42.168  INFO 20384 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 62abe01bee039651
2020-11-16 23:52:42.168  INFO 20384 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1605581562164
2020-11-16 23:52:42.831  INFO 20384 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-11-16 23:52:42.843  INFO 20384 --- [           main] c.m.k.m.OrderEventsProducerApplication   : Started OrderEventsProducerApplication in 4.002 seconds (JVM running for 8.487)
```

To send the message to the *kafka-event-producer* you can use JSON Tool (Postman, Insomnia) or CURL (command-line tool) and send the POST Request to http://localhost:8080/v1/libraryevent.

JSON Request:
```
{
    "orderEventId": null,
    "orderEventType": "NEW",
    "order": {
        "orderId": 1,
        "orderName": "ord-001",
        "orderDevice": "iphone"
    }
}
```

If you prefer to send a request as a command-line.
```
curl -i \
-d '{"orderEventId":null, "orderEventType" : "NEW", "order":{"orderId":1,"orderName":"ord-001","orderDevice":"iphone"}}' \
-H "Content-Type: application/json" \
-X POST http://localhost:8080/v1/libraryevent
```

You can consume the message using the kafka-console-cosumer cli:
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic order-events
```

# Unit/Integration Test

Integration Test 
* TEST_SCENARIO: Send a JSON Message from CLIENT to MICROSERVICE(kafka-event-producer) and the message will be placed on KAFKA into TOPIC: ORDER-EVENTS.
* To run the integration test, run the following test:
__*com.mario.integration.test.controller.OrderEventsControllerIntegrationTest*__

+ Integration Test Flow
[CLIENT] --> [MICROSERVICE  ([CONTROLLER] [KAFKA-PRODUCER])] --> [KAFKA - TOPIC: ORDER-EVENTS]

<br />
Unit Test
* The unit-tests can be found on package *com.mario.kafka.controller.unit.test* 
