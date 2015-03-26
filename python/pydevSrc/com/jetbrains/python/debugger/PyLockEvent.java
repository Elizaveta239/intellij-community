
package com.jetbrains.python.debugger;


public class PyLockEvent extends PyThreadingEvent {
  private final String myId;

  public PyLockEvent(Integer time, String threadId, String name, String id) {
    super(time, threadId, name);
    myId = id;
  }

  @Override
  public String getEventActionName() {
    StringBuilder sb = new StringBuilder();
    switch (myType) {
      case CREATE:
        sb.append(" created");
        break;
      case ACQUIRE_BEGIN:
        sb.append(" acquire started");
        break;
      case ACQUIRE_END:
        sb.append(" acquired");
        break;
      case RELEASE:
        sb.append(" released");
        break;
      default:
        sb.append(" unknown command");
    }
    return sb.toString();
  }

  public String getId() {
    return myId;
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
           " myId= " + myId +
           " " + myFileName +
           " " + myLine +
           "<br>";
  }
}
