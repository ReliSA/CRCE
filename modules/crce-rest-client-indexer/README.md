# REST WS Client Indexer
Module for analysis of (Java) web service applications.
Based on configurations located at ``src/main/resources/definition/<configuration_file.yml>`` will detect and retrieves informations of each called REST service.

Capabality of retrieving information of called endpoints:
- baseURL
- path
- HTTP methods
- responses (name of class + conversion into JSON)
- parameteres (URL parameters + Body data)
- headers (info: https://www.rfc-editor.org/rfc/rfc7231.txt)
  -   control
  -   conditionals
  -   contentNegotiation
  -   authentication
  -   requestContext
  -   representation
  -   response
- calledFrom (method from which endpoint was called)

## Tests

Tests are powered by JUnit tests. They are covering these frameworks and APIs:

- Spring
  - `WebClient` and `RestTemplate`
- JAX-RS API (client part)

## Dependencies
 - junit v4.11
 - log4j v1.2.17
 - asm v7.1
 - jackson-databind (provided)
 - jackson-dataformat-yaml (provided)
 - org.apache.felix.dependencymanager
 - org.apache.felix.bundlerepository
 - crce-core (CRCE module)

## Tips
In order to run this module in so called stand-alone mode run `mvn package -f=pom-standalone.xml` in the root of the project. This will create executable .jar file with *standalone* suffix.