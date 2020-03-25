# Notes on conversion to Appsody

- run `appsody init java-spring-boot2`
- remove generated `src` and replace with `SpringContainerMS/src`
- copy needed dependencies into generated `pom.xml`

### Getting tests to run

- Run backing services (Postgres, Kafka) in local Kubernetes cluster with fixed NodePorts
- edit `setenv.sh` from refarch-kc project so that `KAFKA_BROKERS` and `POSTGRESQL_URL` point to `host.docker.internal:<NodePort>`
- run `appsody test --docker-options "-e KAFKA_BROKERS -e POSTGRESQL_PWD -e POSTGRESQL_USER -e POSTGRESQL_URL"`

### Current problems

Full output in [test-output2.txt](./test-output2.txt)

1 - HealthEndpointTest missing some annotation?
```
[Container] [INFO] Running it.container.HealthEndpointTest
[Container] 15:24:17.608 [main] DEBUG org.springframework.test.context.junit4.SpringJUnit4ClassRunner - SpringJUnit4ClassRunner constructor called with [class it.container.HealthEndpointTest]
[Container] 15:24:17.627 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating CacheAwareContextLoaderDelegate from class [org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate]
[Container] 15:24:17.675 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating BootstrapContext using constructor [public org.springframework.test.context.support.DefaultBootstrapContext(java.lang.Class,org.springframework.test.context.CacheAwareContextLoaderDelegate)]
[Container] 15:24:17.731 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating TestContextBootstrapper for test class [it.container.HealthEndpointTest] from class [org.springframework.boot.test.context.SpringBootTestContextBootstrapper]
[Container] 15:24:17.784 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Neither @ContextConfiguration nor @ContextHierarchy found for test class [it.container.HealthEndpointTest], using SpringBootContextLoader
[Container] 15:24:17.799 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [it.container.HealthEndpointTest]: class path resource [it/container/HealthEndpointTest-context.xml] does not exist
[Container] 15:24:17.802 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [it.container.HealthEndpointTest]: class path resource [it/container/HealthEndpointTestContext.groovy] does not exist
[Container] 15:24:17.803 [main] INFO org.springframework.test.context.support.AbstractContextLoader - Could not detect default resource locations for test class [it.container.HealthEndpointTest]: no resource found for suffixes {-context.xml, Context.groovy}.
[Container] 15:24:17.806 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils - Could not detect default configuration classes for test class [it.container.HealthEndpointTest]: HealthEndpointTest does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
[Container] 15:24:17.983 [main] DEBUG org.springframework.test.context.support.ActiveProfilesUtils - Could not find an 'annotation declaring class' for annotation type [org.springframework.test.context.ActiveProfiles] and class [it.container.HealthEndpointTest]
[Container] [ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.92 s <<< FAILURE! - in it.container.HealthEndpointTest
[Container] [ERROR] initializationError(it.container.HealthEndpointTest)  Time elapsed: 0.012 s  <<< ERROR!
[Container] java.lang.IllegalStateException: Unable to find a @SpringBootConfiguration, you need to use @ContextConfiguration or @SpringBootTest(classes=...) with your test
```

2 - testGetContainers does not get expected output (looks like it expects `[` as first character?)
```
[Container] {"content":[{"id":"c1","latitude":0.0,"longitude":0.0,"type":"Reefer","status":"Empty","brand":"Brand","currentCity":"Oakland","capacity":100,"createdAt":"2020-03-25T15:24:44.678+0000","updatedAt":"2020-03-25T15:24:44.678+0000"},{"id":"c2","latitude":0.0,"longitude":0.0,"type":"Reefer","status":"Empty","brand":"Brand","currentCity":"Oakland","capacity":100,"createdAt":"2020-03-25T15:24:44.906+0000","updatedAt":"2020-03-25T15:24:44.929+0000"}],"pageable":{"sort":{"sorted":false,"unsorted":true,"empty":true},"pageSize":20,"pageNumber":0,"offset":0,"paged":true,"unpaged":false},"totalPages":1,"totalElements":2,"last":true,"number":0,"size":20,"numberOfElements":2,"sort":{"sorted":false,"unsorted":true,"empty":true},"first":true,"empty":false}
[Container] [ERROR] Tests run: 2, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 11.562 s <<< FAILURE! - in it.container.kafka.ContainerAPITest
[Container] [ERROR] testGetContainers(it.container.kafka.ContainerAPITest)  Time elapsed: 0.533 s  <<< FAILURE!
[Container] java.lang.AssertionError: Invalid response from server : {"content":[{"id":"c1","latitude":0.0,"longitude":0.0,"type":"Reefer","status":"Empty","brand":"Brand","currentCity":"Oakland","capacity":100,"createdAt":"2020-03-25T15:24:44.678+0000","updatedAt":"2020-03-25T15:24:44.678+0000"},{"id":"c2","latitude":0.0,"longitude":0.0,"type":"Reefer","status":"Empty","brand":"Brand","currentCity":"Oakland","capacity":100,"createdAt":"2020-03-25T15:24:44.906+0000","updatedAt":"2020-03-25T15:24:44.929+0000"}],"pageable":{"sort":{"sorted":false,"unsorted":true,"empty":true},"pageSize":20,"pageNumber":0,"offset":0,"paged":true,"unpaged":false},"totalPages":1,"totalElements":2,"last":true,"number":0,"size":20,"numberOfElements":2,"sort":{"sorted":false,"unsorted":true,"empty":true},"first":true,"empty":false}
[Container]     at it.container.kafka.ContainerAPITest.testGetContainers(ContainerAPITest.java:47)
```

3 - KafkaClientTest can't connect (looks like a `localhost` URL is hard-coded in test?) 
```
[Container] 2020-03-25 15:25:56.422  WARN 195 --- [           main] org.apache.kafka.clients.NetworkClient   : [Consumer clientId=consumer-13, groupId=testGroup2] Connection to node -1 could not be established. Broker may not be available.
[Container] [ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 59.992 s <<< FAILURE! - in it.container.kafka.KafkaClientTest
[Container] [ERROR] testAutoCommit(it.container.kafka.KafkaClientTest)  Time elapsed: 59.99 s  <<< ERROR!
[Container] org.apache.kafka.common.errors.TimeoutException: Timeout expired while fetching topic metadata
```