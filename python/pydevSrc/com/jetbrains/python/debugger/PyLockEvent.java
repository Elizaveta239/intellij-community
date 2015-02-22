
package com.jetbrains.python.debugger;


public class PyLockEvent extends PyThreadingEvent {
  EVENT_TYPE myType;

  public PyLockEvent(String threadId, EVENT_TYPE type) {
    super(threadId);
    myType = type;
  }

  public enum EVENT_TYPE {
    CREATE, ACQUIRE, RELEASE
  }

  @Override
  public boolean isThreadEvent() {
    return false;
  }

  @Override
  public boolean isLockEvent() {
    return true;
  }
}
