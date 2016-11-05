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

The motivation of this project is rethinking how tools developers take for granted might be implemented with the tools avoidable today. That is, how might common command line tools be implemented in the functional paradigm?

grepcl has two components:
1. A parsing front-end, which uses a parsing combinator library called Kern (https://github.com/blancas/kern) that resembles Haskell's Parsec. The user-defined regular expression is parsed into a stream of valid characters.
2. A finite state machine representation of the user-defined regular expression. This part uses Automat(https://github.com/ztellman/automat), a finite state machine combinator.

Connecting these two components, which are used in sequence, is a data transformation function that changes the stream of characters into code to be processed by the finite state machine combinator segment.

## Future
1. tools.cli/cli might soon be deprecated, and is recommended not to be included in new applications; replace with tools.cli/parse-opts when possible
2. fix command line interface help option
3. fix visual output so text is rendered

## Dependancies

Notable dependancies include the Kern parsing combinator (https://github.com/blancas/kern) and Automat (https://github.com/ztellman/automat), a combinator for representation of finite state machines.

