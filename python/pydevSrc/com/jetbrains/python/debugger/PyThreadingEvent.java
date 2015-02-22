
package com.jetbrains.python.debugger;


public abstract class PyThreadingEvent {
  String myThreadId;

  public PyThreadingEvent(String threadId) {
    myThreadId = threadId;
  }

  public abstract boolean isThreadEvent();

  public abstract boolean isLockEvent();



}
