
package com.jetbrains.python.debugger;


public class PyThreadEvent extends PyThreadingEvent {

  public PyThreadEvent(Integer time, String threadId) {
    super(time, threadId);
  }

  @Override
  public boolean isThreadEvent() {
    return true;
  }

  @Override
  public boolean isLockEvent() {
    return false;
  }

  @Override
  public String toString() {
    return myTime + " " + myThreadId + " PyThreadEvent " +
           "myType=" + myType +
           " " + myInfo +
           '\n';
  }
}
