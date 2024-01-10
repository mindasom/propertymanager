# Property Manager

contains 3 packages, propertymanager, propertymonitor, propertyutil

#### propertymanager

is a server application that maintains properties

#### propertymonitor

is a client application that read new property files and send the new properties to the server, propertymanager

#### propertyutil

is a shared library

## build & run

in order to build projects, you need maven and jdk 18

### build propertyutil

```bash
mvn package
mvn install 
```

### build & run propertymanager

```bash
mvn spring-boot:run
```
