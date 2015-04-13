
package com.jetbrains.python.debugger;


import java.util.List;

public abstract class PyThreadingEvent extends PyLogEvent {
  public enum EventType {
    CREATE, ACQUIRE_BEGIN, ACQUIRE_END, RELEASE, START, JOIN, STOP
  };
  protected Integer myTime;
  protected String myThreadId;
  protected EventType myType;

  public PyThreadingEvent(Integer time, String threadId, String name) {
    myTime = time;
    myThreadId = threadId;
    myName = name;
  }

  public void setType(EventType type) {
    myType = type;
  }

  public String getThreadId() {
    return myThreadId;
  }

  public EventType getType() {
    return myType;
  }

  public String getThreadName() {
    return myName;
  }

  public abstract String getEventActionName();

  public abstract boolean isThreadEvent();

  public abstract boolean isLockEvent();

}
