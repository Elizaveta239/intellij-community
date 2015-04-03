/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.debugger.threading.tool.graph.ui.elements;

import com.jetbrains.python.debugger.threading.tool.graph.GraphSettings;
import com.jetbrains.python.debugger.threading.tool.graph.ui.ThreadState;
import com.jetbrains.python.debugger.threading.tool.graph.ui.StoppedThreadState;

import java.awt.*;

public class SimpleDrawElement extends DrawElement {

  public SimpleDrawElement(Color color, ThreadState before, ThreadState after) {
    super(color, before, after);
  }

  @Override
  public DrawElement getNextElement() {
    return new SimpleDrawElement(myColor, myAfter, myAfter);
  }

  @Override
  public void drawElement(Graphics g, int padding) {
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int x = Math.round((padding + 0.5f) * GraphSettings.NODE_WIDTH);

    if (myBefore instanceof StoppedThreadState) {
      return;
    }

    myBefore.prepareStroke(g2);
    g2.drawLine(x, 0, x, Math.round(GraphSettings.CELL_HEIGH * 0.5f));
    myAfter.prepareStroke(g2);
    g2.drawLine(x, Math.round(GraphSettings.CELL_HEIGH * 0.5f), x, GraphSettings.CELL_HEIGH);

  }
}
