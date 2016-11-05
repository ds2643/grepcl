# grepcl

An function implementation of a standard UNIX utility, grep (global regular expression parser), in Clojure. This tool leverages parsing and finite state machine combinators, which are used to parse a user-defined regex

This project is currently at an alpha stage, with only base features implemented. Several more features (detailed in Future section) are yet to be implemented

## Installation

Since this tool is meant to be used from the command-line, compiling to Java Machine (JVM) bytecode is necessary. Doing so with Clojure's Leiningen (http://leiningen.org/) is easy:

```
$ lein compile # from inside the project directory
$ lein uberjar # make jar, dependancies included
$ java -jar grepcl-0.1.0-SNAPSHOT.jar [command line args] # run
```

## Use

## Approach

## Future

## Dependancies


