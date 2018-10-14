[![CircleCI](https://circleci.com/gh/udaychandra/susel.svg?style=svg)](https://circleci.com/gh/udaychandra/susel)

## Susel
<em>Su</em>per <em>Se</em>rvice <em>L</em>oader: Super charge the module aware service loader in Java 11. 
Susel is a light weight library that helps one build Java native (JPMS) modular applications.

## Introduction
Service loader mechanism of Java, introduced in Java 6 and revised in Java 9, is used to locate and load services. 
A service is a well known interface or class (usually abstract). A service provider is a concrete implementation of a service. 
The ServiceLoader class is a facility to load service providers that implement a given service. 
A Java native module can declare that it uses a specific service in its module description (module-info.java). The module can then use the ServiceLoader to locate and load the service providers deployed in the run time environment.

Susel builds on this service loading mechanism and provides a few useful features. 
Susel enables you to specify required or optional service references (through annotations) that a a given service provider might need to operate. 
Partly inspired by OSGI, Susel provides an "activate" annotation that can be used by a service provider to do some initiation work or to read configuration from a global context map passed to it by Susel.  

## Basic Usage
If you are using a build tool like Maven or Gradle, add the following dependency to access Susel API:

- Maven pom.xml
  ```xml
   <dependency>
     <groupId>io.github.udaychandra</groupId>
     <artifactId>susel</artifactId>
     <version>0.1.1</version>
   </dependency>
   ```

- Gradle build.gradle
  ```groovy
   dependencies {
     compile 'io.github.udaychandra:susel:0.1.1'
   }
   ```

> Note that Susel requires Java 11

Begin by defining a service interface.

```java
package com.example.svc;

public interface HelloService {
    String hello(String id);
}

```   
Create a service provider by implementing the service interface.

```java
package com.example.svc.name;

public class HelloNameService implements HelloService {
    private UserService userService;
    
    @ServiceReference(cardinality = Cardinality.ONE)
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public String hello(String id) {
        return "Hello " + userService.getName(id);
    }
}
```

Update the module descriptor (module-info.java)
```java
module com.example.svc.name {
    exports com.example.svc.name;

    requires com.example.svc;

    provides com.example.svc.HelloService 
        with com.example.svc.name.HelloNameService;
}

```

Now, a consuming client of the service interface can delegate the lookup, preparation and loading to Susel. 
Say the module descriptor of the client looks something like this:
```java
module com.example.client {
    exports com.example.client;

    requires com.example.svc;
    requires io.github.udaychandra.susel;

    uses com.example.svc.HelloService;
}
```

The client can then activate Susel once and start loading services. 
For example, to load the HelloService one can do this in the application's main method:
```java
Susel.activate(Map.of());
var helloService = Susel.get(HelloService.class);
```

Susel will take care of loading a service provider that implements the hello service.
When it finds the HelloNameService, it will ensure that at least one service provider that implements 
the required UserService reference is found and injected, activates HelloNameService and returns the now ready to use HelloNameService. 

Susel relies on the presence of a metadata file (META-INF/susel.metadata) in a module to figure out the service references used by a service provider, 
their cardinalities and the method that should be invoked to activate the service provider. This metadata file should be generated during compile time.

There's a gradle [plugin](https://github.com/udaychandra/susel-gradle-plugin) that can automate the generation and packaging of these metadata files. 
The plugin calls the Susel tool which can be manually invoked as well.

> A maven plugin is in the works

## Development
This is a community project. All contributions are welcome.

To start contributing, do the following:
* Install JDK 11
* Fork or clone the source code
* Run the build using the gradle wrapper
```bash
gradlew clean build
```

## License
Apache License 2.0
