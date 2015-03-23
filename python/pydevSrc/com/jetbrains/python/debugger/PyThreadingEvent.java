
package com.jetbrains.python.debugger;


public abstract class PyThreadingEvent {
  public enum EVENT_TYPE {
    CREATE, ACQUIRE_BEGIN, ACQUIRE_END, RELEASE, START, JOIN, STOP
  };
  Integer myTime;
  String myThreadId;
  String myName;
  EVENT_TYPE myType;
  String myFileName;
  Integer myLine;

  public PyThreadingEvent(Integer time, String threadId, String name) {
    myTime = time;
    myThreadId = threadId;
    myName = name;
  }

  public void setType(EVENT_TYPE type) {
    myType = type;
  }

  public void setFileName(String fileName) {
    myFileName = fileName;
  }

  public String getFileName() {
    return myFileName;
  }

  public void setLine(Integer line) {
    myLine = line;
  }

  public Integer getLine() {
    return myLine;
  }

  public String getThreadId() {
    return myThreadId;
  }

  public EVENT_TYPE getType() {
    return myType;
  }

  public String getThreadName() {
    return myName;
  }

  public abstract String getEventName();

  public abstract boolean isThreadEvent();

  public abstract boolean isLockEvent();

}
