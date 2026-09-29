# Java Application Deployment

This document explains how to package a Maven-based Java application into a
standalone application and installer using `jpackage`.

---

## General Packaging Workflow

The overall workflow is:

```text
Java source code
      ↓
Maven compile
      ↓
Maven Shade Plugin
      ↓
Executable JAR
      ↓
Maven Antrun Plugin
      ↓
Prepare JAR for jpackage
      ↓
Maven Exec Plugin
      ↓
Run jpackage
      ↓
Application Image / Installer
```


---

## Manual Deployment

### Step 1: Compile the application

Maven compiles the Java source code into `.class` files.

```bash
./mvnw compile
```

---

### Step 2: Create an executable JAR

The Maven Shade Plugin packages:

* Application classes
* Runtime dependencies such as Gson
* A manifest containing the main class

Example:

```bash
./mvnw package
```

Result:

```text
target/
└── task-manager-1.0-SNAPSHOT.jar
```

The JAR can be tested with:

```bash
java -jar target/task-manager-1.0-SNAPSHOT.jar
```

---

### Step 3: Prepare the JAR for jpackage

`jpackage` expects an input directory containing the application JAR.  
You copy JAR file to this input folder.

Later on, you can automate this process with Maven plugins:

```text
target/
└── package-input/
    └── task-manager-1.0-SNAPSHOT.jar
```

---

### Step 4: Run jpackage

`jpackage` takes the JAR and creates a self-contained application.

For example:

```bash
jpackage \
  --type app-image \
  --name TaskManager \
  --input target/package-input \
  --main-jar task-manager-1.0-SNAPSHOT.jar \
  --main-class com.badlogic.taskmanager.Main \
  --dest target/dist
```

The application image contains:

* Application launcher
* Application JAR
* Java runtime
* Supporting native files
* Application configuration

The user therefore does not need to install Java separately.

---

### Step 5: Create an installer

Instead of:

```bash
--type app-image
```

We use an installer type supported by the operating system.

For example:

```bash
jpackage \
  --type deb \
  --name TaskManager \
  --input target/package-input \
  --main-jar task-manager-1.0-SNAPSHOT.jar \
  --main-class com.badlogic.taskmanager.Main \
  --dest target/dist
```

Result:

```text
target/
└── dist/
    └── TaskManager_....deb
```

---

## Maven Plugins Used

### Maven Shade Plugin

Purpose:

> Create a JAR containing the application and its dependencies.

In this project it packages:

```text
Your application
     +
Gson
     ↓
task-manager-1.0-SNAPSHOT.jar
```

It also sets the application's main class in the manifest.

---

### Maven Antrun Plugin

Purpose:

> Execute Ant build tasks from Maven.

In this project it is used for file operations:

```text
mkdir → create directory
copy  → copy the JAR
```

The `package-input` directory is created automatically during the build.

---

### Maven Exec Plugin

Purpose:

> Execute an external program or command from Maven.

This allows us to automatically execute `jpackage` during the build:

```bash
./mvnw package
```

---

## Automatic Deployment

We simply run:

```bash
./mvnw clean package
```

The deployment process is approximately:

```text
1. Maven builds the project
        ↓
2. Shade creates the executable JAR
        ↓
3. Antrun creates package-input/
        ↓
4. Antrun copies the JAR
        ↓
5. Exec runs jpackage
        ↓
6. jpackage creates the final package
```

Example final structure:

```text
target/
├── task-manager-1.0-SNAPSHOT.jar
│
├── package-input/
│   └── task-manager-1.0-SNAPSHOT.jar
│
└── dist/
    └── TaskManager_....deb
```

---

## Application Launcher vs Application Image vs Installer

These are related but different things.

### Application Launcher

The launcher is the executable that starts the Java application.

Example:

```text
TaskManager/bin/TaskManager
```

When executed:

```bash
./TaskManager/bin/TaskManager
```

it starts the application using the bundled Java runtime.

The launcher is **not the whole application**.

---

### Application Image

The application image is the complete runnable application directory.

Example:

```text
TaskManager/
├── bin/
│   └── TaskManager
│
└── lib/
    ├── app/
    │   └── task-manager-1.0-SNAPSHOT.jar
    │
    └── runtime/
```

It contains:

```text
Launcher
   +
Application JAR
   +
Java Runtime
   +
Supporting files
```

An application image can be distributed directly.

The user can run the launcher without installing Java separately.

---

### Installer

An installer is a package used to install the application onto the user's
system.

Examples:

```text
Linux → .deb
Windows → .exe / .msi
macOS → .pkg
```

The installer installs an application image onto the system.

For example:

```text
TaskManager.deb
      ↓
   install
      ↓
Application Image
      ↓
Installed TaskManager
```

The installer is therefore **not the same thing as the application launcher**.

