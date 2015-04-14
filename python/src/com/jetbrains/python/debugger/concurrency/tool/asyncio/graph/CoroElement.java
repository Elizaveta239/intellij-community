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
package com.jetbrains.python.debugger.concurrency.tool.asyncio.graph;
import com.jetbrains.python.debugger.concurrency.tool.GraphSettings;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CoroElement extends DrawElement {
  protected DrawElement myParent;

  public CoroElement(Color color) {
    super(color);
  }

  @Override
  public DrawElement getNextElement() {
    return new EmptyElement(null);
  }

  @Override
  public void drawElement(Graphics g, int padding) {
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(myColor);
    double r = GraphSettings.CELL_HEIGH * 0.25;
    double newX = Math.round((padding + 0.5f) * GraphSettings.NODE_WIDTH) - r;
    double newY = GraphSettings.CELL_HEIGH * 0.5 - r;
    RoundRectangle2D rectangle2D = new RoundRectangle2D.Double(newX, newY,
                                                               GraphSettings.CELL_HEIGH * 0.5,
                                                               GraphSettings.CELL_HEIGH * 0.5,
                                                               50, 50);
    g2.fill(rectangle2D);
  }
}
