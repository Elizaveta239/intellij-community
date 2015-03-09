
package com.jetbrains.python.debugger;


public class PyThreadEvent extends PyThreadingEvent {


  public PyThreadEvent(Integer time, String threadId, String name) {
    super(time, threadId, name);
  }

  @Override
  public String getEventName() {
    StringBuilder sb = new StringBuilder();
    sb.append("Thread ");
    switch (myType) {
      case CREATE:
        sb.append(" created");
        break;
      case START:
        sb.append(" started");
        break;
      case JOIN:
        sb.append(" joined");
        break;
      default:
        sb.append(" unknown command");
    }
    return sb.toString();
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
           " " + myInfo +
           "<br>";
  }
}
