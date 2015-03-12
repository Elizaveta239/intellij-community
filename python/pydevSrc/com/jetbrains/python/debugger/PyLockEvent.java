
package com.jetbrains.python.debugger;


public class PyLockEvent extends PyThreadingEvent {

  public PyLockEvent(Integer time, String threadId, String name) {
    super(time, threadId, name);
  }

  @Override
  public String getEventName() {
    StringBuilder sb = new StringBuilder();
    sb.append("Lock ");
    switch (myType) {
      case CREATE:
        sb.append(" created");
        break;
      case ACQUIRE:
        sb.append(" acquared");
        break;
      case RELEASE:
        sb.append(" released");
        break;
      default:
        sb.append(" unknown command");
    }
    return sb.toString();
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
           " " + myFileName +
           " " + myLine +
           "<br>";
  }
}
