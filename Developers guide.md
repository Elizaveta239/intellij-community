# Developers guide

This document describes architecture of the Thread analyser tool.

## The debugger in the PyCharm IDE

The PyCharm IDE doesn't use `pdb`, but implements its own debugger. 
The debugger in Pycharm IDE consists of two parts: tracing part (written in Python) and IDE part (written in Java). These parts
communicate via sockets.

Python side of the debugger in PyCharm IDE traces programs using `sys.settrace(tracefunc)` function
([see documentation](https://docs.python.org/2/library/sys.html#sys.settrace)). During the program execution, tracing function processes
 events in program and send information about them to the Java side. Also it receives information about user's behavior.
 All suitable files are situated in `/python/helpers/pydev` package.
 The main debugging class is `PyDB` class in `pydevd.py`. And the tracing for the frames in program is implemented in the
  `PyDBFrame` class in `pydevd_frame.py`.

The Java side of the debugger implements everything, that user can see in the IDE during the debug process.
The main logic of the Java side of the Python debugger is situated in the packages `com.jetbrains.python.debugger.pydev` and `com.jetbrains.python.debugger`. The
debug process is implemented in the `PyDebugProcess` class and debugger is implemented in `RemoteDebugger` and `MultiProcessDebugger`
classes. 

# General architecture of the Thread analyser

A Thread analyser also consists of two parts: 
* Logger, which patches some modules in debugger on the Python side and collects log
* IDE part of tool, which builds toolwindow on the Java side.
  
# Python side

Logger is situated on the Python side of debugger. It is turned on by checkbox in Python debugger settings. If logger is turned on, it 
wraps interesting modules (at the moment, it is `threading` module) and adds logging for selected methods. Logger is situated in the
`pydevd_thread_analyser` package.

### Wrapper

Wrapper is used for the Lock objects only, cause in the threading package Lock is not a separate class, but a factory.
 
### Logger

The part of logger which is really used in a log creation is based on the tracing in the Python debugger. Debugger's trace function allows 
to get information about the current frame and execution event. So, it becomes possible to get information about interesting functions
(for example, functions from `threading` module) and moreover get standart debug information (like file and line, where this method was 
called, stackframe, and so on). All this information is taken from the frames in `ThreadingLog` class in `pydevd_thread_logger.py` and than 
is sent via sockets to the Java side.

# Java side

The Java side of the Thread analyser also consists of two parts: Threading log manager and Toolwindow (UI). It is situated in the 
'com.jetbrains.python.debugger.threading' package.

Threading log manager collects information received from the Python side. It is implemented in `PyThreadingLogManagerImpl`. Also it 
listens to the debug process and updates visualization.

A Toolwindow is implemented in the `threading.tool.ui` package and the most important parts of it is `ThreadingView` class. A Threading view is named
*Concurrent activities diagram* and contains a table implemented in `ThreadingTable` class. Each row of this table corresponds to a threading event and
 each row contains information about it. The most interesting row is a row which contains a Threading graph implemented in 
 `threading.tool.graph` package.
 
Graph is managed by `GraphManager` which gets the current log state and builds representation of the graph row by row. `GraphCellRenderer`
draws graph with the representation obtained from `GraphManager`.

### Navigation to source code

The source information for every threading event is collected in a log manager. The mouse listener for the corresponding
 row in the table creates navigatable object provided by Intellij platform and navigates to the source position.
