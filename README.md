# Audit-lib microservice
This project presents starter lib for logging.
Provides custom annotation (method level) to log method execution.
Can log incoming and outgoing http-requests.

## Install
### Preferenced requirements
* Java 21
* Maven 3.9.9
* Spring Boot 3.5.3
* Docker

### Steps to install project
1. Clone repository.
```shell
git clone https://github.com/NiRO-bb/audit-lib.git
```

2. Install to local maven repository.
```shell
maven clean install
```

## Usage
1. Use in your project. Add maven dependency (version can be changed).
```
<dependency>
    <groupId>com.example</groupId>
	<artifactId>audit-lib-spring-boot-starter</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

2. You can override some properties.
* audit-lib.annotationConsoleLevel (default - OFF) - defines minimum logging level of logs written to console due to annotations (default - no log can be written) 
* audit-lib.annotationFileLevel (default - OFF) - defines minimum logging level of logs written to file due to annotations (default - no log can be written)
* audit-lib.httpConsoleLevel (default - OFF) - defines minimum logging level of logs written to console due to http-requests (default - no log can be written)
* audit-lib.httpFileLevel (default - OFF) - defines minimum logging level of logs written to file due to http-requests (default - no log can be written)
* audit-lib.httpLoggingLevel (default - INFO) - defines actual logging level of logs written anywhere due to http-requests
* audit-lib.annotationKafkaLevel (default - OFF) - defines actual logging level of logs written to Kafka topic due to annotation (default - no log can be written)
* audit-lib.httpKafkaLevel (default - OFF) - defines actual logging level of logs written to Kafka topic due to http-requests (default - no log can be written)
* audit-lib.kafkaMethodTopic (default - audit.methods) - sets Kafka topic name where method logs will be sent
* audit-lib.kafkaRequestTopic (default - audit.requests) - sets Kafka topic name where request logs will be sent
* audit-lib.kafkaPartitionNum (default - 1) - sets Kafka topic partition number
* audit-lib.kafkaReplicationFactor (default - 1) - sets Kafka partition replication factor

## Additionally
1. Run with Docker
```shell
docker compose up
```
You must write .env_dev file with following values (you can use .env_template file from root directory):
* ZOOKEEPER_CLIENT_PORT
* ZOOKEEPER_TICK_TIME
* KAFKA_BROKER_ID
* KAFKA_ZOOKEEPER_CONNECT
* KAFKA_ADVERTISED_LISTENERS
* KAFKA_LISTENERS
* KAFKA_LISTENER_SECURITY_PROTOCOL_MAP
* KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR

<b>!</b> docker-compose.yml uses docker network - 'producer-consumer'. 
This for interaction with other containers (Kafka consumer, for example). But you must create this network manually:
```shell
docker network create producer-consumer
```

## Contributing
<a href="https://github.com/NiRO-bb/audit-lib/graphs/contributors/">Contributors</a>

## License
No license 