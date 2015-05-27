
package com.jetbrains.python.debugger.concurrency.tool.graph.elements;

import com.jetbrains.python.debugger.concurrency.tool.graph.states.ThreadState;

import java.awt.*;

public abstract class DrawElement {

  protected Color myColor;
  protected ThreadState myBefore;
  protected ThreadState myAfter;

  public DrawElement(Color color, ThreadState before, ThreadState after) {
    myColor = color;
    myBefore = before;
    myAfter = after;
  }

  public ThreadState getBefore() {
    return myBefore;
  }

  public ThreadState getAfter() {
    return myAfter;
  }

  @Override
  public boolean equals(Object element) {
    if (!(element instanceof DrawElement)) {
      return false;
    }
    DrawElement other = (DrawElement)element;
    return (this.getBefore().getClass() == other.getBefore().getClass()) &&
           (this.getAfter().getClass() == other.getAfter().getClass());
  }

  public void setAfter(ThreadState state) {
    myAfter = state;
  }

  public abstract DrawElement getNextElement();

  public void drawElement(Graphics g, int padding) {
  }

  public Color getColor() {
    return myColor;
  }

  public void setColor(Color color) {
    myColor = color;
  }
}
