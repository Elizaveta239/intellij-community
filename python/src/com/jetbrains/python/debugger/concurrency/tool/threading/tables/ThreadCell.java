
package com.jetbrains.python.debugger.concurrency.tool.threading.tables;

public class ThreadCell {
  private final String myThreadName;

  public ThreadCell(String threadName) {
    myThreadName = threadName;
  }

  @Override
  public String toString() {
    return myThreadName;
  }
}
