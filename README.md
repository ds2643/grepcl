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

A compiled version of the program may be used from the command line with the following options:
```
$> java -jar grepcl -r [regular expression] -p [src/path] -v [visual] -h [help]

```
In this context, flags -v (visual mode) and -h (help) are optional. Visual mode renders the user-defined regular expression as a graph/finite state machine using graph viz. If users choose, this features may be used to examine their regular expression to ensure correctness.

The syntax of regular expressions is slightly unconventional, differing slightly from conventional regular expression notation. Additionally, some features standard to regular expression are currently not supported. The following examples illustrate how users might definte regular expressions:

1. alpha-numeric pattern: a string consisting of only letters and numbers. e.g., "hello"
2. a wildcard character, '.', which allows any character or letter. e.g., "l.l" allows any character or number in place of '.'.
3. one or more of a single character, denoted by "a+", where a is the character. e.g., "fo+d" accecepts "fod", "food", "foood", etc.
4. zero or more characters, denoted by "a*", where a is the character. e.g., "fo*d" accepts "fd", "fod", "food", "foood", etc.
5. sets of characters, denoted in brackets, indicates that any character from the defined set. e.g., "l[aoeui]l" accepts as its middle character any vowel from the defined set.
6. Features to be added on the horizon are detailed in the future section.

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
4. additional regex support: maybe, not, zero/one or more on a set, ranges etc.
5. improved error handling

## Dependencies

Notable dependancies include the Kern parsing combinator (https://github.com/blancas/kern) and Automat (https://github.com/ztellman/automat), a combinator for representation of finite state machines.

