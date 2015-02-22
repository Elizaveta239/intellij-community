
package com.jetbrains.python.debugger;


public class PyThreadEvent extends PyThreadingEvent {
  public EVENT_TYPE myType;

  public enum EVENT_TYPE {
    CREATE, START, JOIN
  }

  @Override
  public boolean isThreadEvent() {
    return true;
  }

  @Override
  public boolean isLockEvent() {
    return false;
  }


  public PyThreadEvent(String threadId, EVENT_TYPE event) {
    super(threadId);
    myType = event;
  }
}
