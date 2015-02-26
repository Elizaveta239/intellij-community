
package com.jetbrains.python.debugger;


public class PyLockEvent extends PyThreadingEvent {

  public PyLockEvent(Integer time, String threadId) {
    super(time, threadId);
  }

  @Override
  public boolean isThreadEvent() {
    return false;
  }

  @Override
  public boolean isLockEvent() {
    return true;
  }

  @Override
  public String toString() {
    return myTime + " " + myThreadId + " PyLockEvent" +
           " myType=" + myType +
           " " + myInfo +
           '\n';
  }
}
