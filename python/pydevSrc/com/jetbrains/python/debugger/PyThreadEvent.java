
package com.jetbrains.python.debugger;


public class PyThreadEvent extends PyThreadingEvent {
  private String myParentThreadId;


  public PyThreadEvent(Integer time, String threadId, String name) {
    super(time, threadId, name);
  }

  public PyThreadEvent(Integer time, String threadId, String name, String parentThreadId) {
    super(time, threadId, name);
    myParentThreadId = parentThreadId;
  }

  @Override
  public String getEventActionName() {
    StringBuilder sb = new StringBuilder();
    switch (myType) {
      case CREATE:
        sb.append(" created");
        break;
      case START:
        sb.append(" started");
        break;
      case JOIN:
        sb.append(" join called");
        break;
      case STOP:
        sb.append(" stopped");
        break;
      default:
        sb.append(" unknown command");
    }
    return sb.toString();
  }

  public String getParentThreadId() {
    return myParentThreadId;
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
    return myTime + " " + myThreadId + " PyThreadEvent" +
           " myType=" + myType +
           " " + myFileName +
           " " + myLine +
           "<br>";
  }
}
