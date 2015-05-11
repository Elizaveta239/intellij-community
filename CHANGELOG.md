# Changes

## 0.4, 11.05.2015

* Support for Task, Lock and Queue from `asyncio` module
* Remove coroutines support from `asyncio` module
* Support Queue from `asyncio` module
* Deadlocks detection

## 0.3, 18.04.2015

* Support for coroutines from `asyncio` module
* Separate join and stop events for threads
* Fixes in stacktrace panel

## 0.2.1, 04.04.2015

Bug fix release

## 0.2, 31.03.2015

* Lock events added: acquire started and acquired 
* New color scheme in graph for distinguish different thread states:
  * Work without Lock objects
  * Waiting for a Lock object
  * Owning a Lock object
* Stacktrace for every threading event with navigation to source

## 0.1, 13.03.2015

Initial release.
* Support for events from module `threading`:
  * Thread: start, join, stop
  * Lock: acquire, release
* Basic visualization for threading events.
* Navigation to source code.
