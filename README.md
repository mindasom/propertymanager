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

### build & run propertymonitor

```bash
mvn spring-boot:run
```

if running propertymonitor shows an error like 

```bash
SEVERE: Unable to monitor: C:\projects\propertyManager\propertyManager\propertymanager\propertymonitor\properties does not exist
```

create the directory, or add config file as command line argument

propertymanager must be running in order to start propertymonitor.

## configure properties

you can customize propertymanager and propertymonitor using command line argument - property file.

```bash
 mvn spring-boot:run -Dspring-boot.run.arguments=testConfig.properties
```

### configurable properties - propertymanager

```properties
# directory that maintains property files
property.dir = properties
# property manager server port
server.port = 8068
```

### configurable properties - propertymonitor

```properties
# directory that propertymonitor monitors to get new property files
monitor.dir = properties
# java style regex for valid key pattern
key.filter = .+
# property manager url prefix to add/update new properties
server.url = http://localhost:8080/properties/send
```

