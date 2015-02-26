
package com.jetbrains.python.debugger;


public abstract class PyThreadingEvent {
  public enum EVENT_TYPE {
    CREATE, ACQUIRE, RELEASE, START, JOIN
  };
  Integer myTime;
  String myThreadId;
  EVENT_TYPE myType;
  String myInfo;

  public PyThreadingEvent(Integer time, String threadId) {
    myTime = time;
    myThreadId = threadId;
  }

  public void setType(EVENT_TYPE type) {
    myType = type;
  }

  public void setInfo(String info) {
    myInfo = info;
  }

  public abstract boolean isThreadEvent();

  public abstract boolean isLockEvent();

}
