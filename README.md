# Thread analyser

A tool for visualization of multitasking Python programs based on the PyCharm IDE.

## Description

A Thread analyser helps to understand the multitasking Python program execution during debug process.
Some of it's features include:

* Visualization of events in multithreaded programs (programs using module `threading`)
* Navigation to the source code for every threading event

## Installation

A Thread analyser is a part of the PyCharm IDE, so its build instructions concur with
[PyCharm IDE build instructions](https://github.com/JetBrains/intellij-community/blob/master/README.md).

## Usage

A Thread analyser tool is a part of PyCharm IDE. The visualization is available for every Python program during or after debug session in
the PyCharm IDE.

### Collecting log

In order to build diagram for every debug session, the checkbox "Build diagram for multitasking or multithreaded programs " should be checked in
Settings | Build, Execution, Deployment | Python debugger. It is checked by default. If the checkbox is not checked, visualization is
 not available.

### Visualization

The visualization is available in a separate toolwindow named *Thread/task diagram*. The toolwindow contains a table with information about
events and threading graph. Each row in the table corresponds to event in a multithreaded program. At the moment Thread analyser supports
the following types of events:
 
For module `threading`:
* Thread start
* Thread join
* Thread stop
* Lock acquire
* Lock release

### Navigation to source code

Double-click on a row in the table navigates user to the source code of the corresponding threading event.





 



