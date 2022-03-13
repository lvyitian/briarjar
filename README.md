# BriarJar

The aim of the BriarJar project is to provide a prototype of a simple messenger that can be run both with a graphical user interface and within a terminal on major GNU/Linux distributions. The messenger functionalities consist of the registration of an account, a contact management as well as a text communication with a desired conversation partner.

### Requirements

- A GNU/Linux Distribution
- [Java Development Kit 17](https://jdk.java.net/17/) or higher
- For GUI Mode: A display server like Xorg or Wayland

### Usage

Make sure you have the correct Java version: 
```
java -version
```

To start in GUI Mode, simply run the FatJAR with
```
java -jar --add-opens=java.base/java.lang.reflect=ALL-UNNAMED briarjar-0.1-all.jar
```

The TUI Mode can be invoked with either  
```
java -jar briarjar-0.1-all.jar -t
```
or
```
java -jar briarjar-0.1-all.jar --tui
```

### Options

```
BriarJar version 1.00 Copyright (C) 2022 BriarJar Project Team

Options:
-t, --tui        Start in TUI mode (default is GUI)

-h, --help       Show (this) help menu
-v, --verbose    Show all logs (floods the TUI, discouraged)
```

### Building

The briar-as-subproject submodule has to be cloned too, when cloning BriarJar. 
```
git clone --recurse-submodules git@code.briarproject.org:briar/briarjar 
```

Simply run the ShadowJar Gradle Task to compile the FatJAR.

```
./gradlew shadowJar
```

ShadowJar will create the jar-file in the directory `build/libs/`

For convenience, you can use the `start_briarjar.sh` script after building.
```
./start_briarjar.sh [options]
```

## Disclaimer

This is a diploma project and is not intended for public use at the moment. Use at your own risk.
