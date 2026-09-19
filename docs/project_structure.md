# Project Structure

```text
task-manager/
├── .mvn/
│   ├── wrapper/
│   │   └── maven-wrapper.properties
│   └── maven.config
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
├── mvnw
└── pom.xml
```

### `.mvn/`

Maven-specific project configuration.

* **`.mvn/wrapper/`** — contains configuration for the Maven Wrapper.
* **`maven-wrapper.properties`** — specifies which Maven version the project should use and where Maven should be downloaded from.

### `.mvn/maven.config`

Provides default command-line options for Maven.  
We configured:

```text
-Dmaven.repo.local=.mvn/repository
```

This tells Maven to store downloaded dependencies in the project's `.mvn/repository/` directory.

### `src/main/java/`

Contains the **actual application source code**.

For example:

```text
src/main/java/com/example/taskmanager/
├── Main.java
├── Task.java
└── TaskManager.java
```

### `src/test/java/`

Contains **test source code**, such as JUnit tests.

```text
src/test/java/com/example/taskmanager/
└── TaskManagerTest.java
```

Maven automatically knows that code under `src/main/java` is production code and code under `src/test/java` is test code.

### `mvnw`

The **Maven Wrapper script** for Linux/macOS.  
Instead of requiring Maven to be installed globally, we use `./mvnw`.  
The wrapper downloads and uses the Maven version specified by the project.

### `pom.xml`

The **main Maven project configuration file**.

Define the project and its build requirements for Maven:

* Project name and version
* Java version
* Dependencies such as **Gson** and **JUnit**
* Build plugins

### The big picture

```text
pom.xml
   │
   ├── Dependencies → Gson, JUnit
   ├── Java version
   └── Build configuration
          │
          ↓
       ./mvnw
          │
          ├── compile → src/main/java
          ├── test    → src/test/java
          └── package → builds the application
```

**When creating another Maven project:** create the standard `src` structure, `pom.xml`, and Maven Wrapper. Then most project-specific configuration goes into `pom.xml`.
