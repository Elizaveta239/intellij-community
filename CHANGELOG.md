# Changes

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
