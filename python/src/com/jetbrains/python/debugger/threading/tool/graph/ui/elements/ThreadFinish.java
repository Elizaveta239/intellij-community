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
import com.jetbrains.python.debugger.threading.tool.graph.ui.DrawElement;

import java.awt.*;

/**
 * Created by user on 3/23/15.
 */
public class ThreadFinish extends DrawElement {

  public ThreadFinish(Color color) {
    super(color);
  }

  public ThreadFinish() {
  }

  @Override
  public DrawElement getNextElement() {
    return new Empty();
  }

  @Override
  public void drawElement(Graphics g, int padding) {
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int x = Math.round((padding + 0.5f) * GraphSettings.NODE_WIDTH);

    g2.setColor(GraphSettings.USE_STD_COLORS? GraphSettings.BASIC_COLOR: myColor);
    g2.setStroke(new BasicStroke(GraphSettings.STROKE_BASIC));
    g2.drawLine(x, 0, x, Math.round(GraphSettings.CELL_HEIGH * 0.5f));
    drawEvent(g, padding);
  }
}
