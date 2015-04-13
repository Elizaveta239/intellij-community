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
package com.jetbrains.python.debugger;

import java.util.List;


public abstract class PyLogEvent {
  protected String myFileName;
  protected Integer myLine;
  protected String myName;

  public void setFileName(String fileName) {
    myFileName = fileName;
  }

  public String getFileName() {
    return myFileName;
  }

  public void setLine(Integer line) {
    myLine = line;
  }

  public Integer getLine() {
    return myLine;
  }

  protected List<PyStackFrameInfo> myFrames;

  public List<PyStackFrameInfo> getFrames() {
    return myFrames;
  }

  public void setFrames(List<PyStackFrameInfo> frames) {
    myFrames = frames;
  }
}
