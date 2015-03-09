
package com.jetbrains.python.debugger;


public abstract class PyThreadingEvent {
  public enum EVENT_TYPE {
    CREATE, ACQUIRE, RELEASE, START, JOIN
  };
  Integer myTime;
  String myThreadId;
  String myName;
  EVENT_TYPE myType;
  String myInfo;

  public PyThreadingEvent(Integer time, String threadId, String name) {
    myTime = time;
    myThreadId = threadId;
    myName = name;
  }

  public void setType(EVENT_TYPE type) {
    myType = type;
  }

  public void setInfo(String info) {
    myInfo = info;
  }

  public String getThreadId() {
    return myThreadId;
  }

  public String getThreadName() {
    return myName;
  }

  public abstract String getEventName();

  public abstract boolean isThreadEvent();

  public abstract boolean isLockEvent();

}
