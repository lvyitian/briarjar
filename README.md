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

To start in GUI Mode, simply run the FatJAR
```
java -jar briarjar-0.1-all.jar
```

TUI Mode can be invoked with either  
```
java -jar briarjar-0.1-all.jar tui
```
or
```
java -jar briarjar-0.1-all.jar --tui
```

### Building

The briar-as-subproject submodule has to be cloned too, when cloning BriarJar. 
```
git clone --recurse-submodules git@code.briarproject.org:briar/briarjar 
```

Simply run the ShadowJAR Gradle Task to compile the FatJAR.



## Disclaimer

This is a diploma thesis and is not intended for public use at the moment. Use at your own risk.