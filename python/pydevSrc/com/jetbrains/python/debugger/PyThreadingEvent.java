
package com.jetbrains.python.debugger;


import java.util.List;

public abstract class PyThreadingEvent {
  public enum EVENT_TYPE {
    CREATE, ACQUIRE_BEGIN, ACQUIRE_END, RELEASE, START, JOIN, STOP
  };
  protected Integer myTime;
  protected String myThreadId;
  protected String myName;
  protected EVENT_TYPE myType;
  protected String myFileName;
  protected Integer myLine;
  protected List<PyStackFrameInfo> myFrames;

  public List<PyStackFrameInfo> getFrames() {
    return myFrames;
  }

  public void setFrames(List<PyStackFrameInfo> frames) {
    myFrames = frames;
  }

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

  public abstract String getEventActionName();

  public abstract boolean isThreadEvent();

  public abstract boolean isLockEvent();

}
