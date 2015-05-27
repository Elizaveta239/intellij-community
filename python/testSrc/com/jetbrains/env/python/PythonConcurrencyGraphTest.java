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
package com.jetbrains.env.python;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.jetbrains.env.PyEnvTestCase;
import com.jetbrains.env.python.debug.PyDebuggerTask;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyColorManager;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.PyAsyncioLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphManager;
import com.jetbrains.python.debugger.concurrency.tool.graph.elements.DrawElement;
import com.jetbrains.python.debugger.concurrency.tool.graph.elements.EventDrawElement;
import com.jetbrains.python.debugger.concurrency.tool.graph.elements.SimpleDrawElement;
import com.jetbrains.python.debugger.concurrency.tool.graph.states.*;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;

import java.util.ArrayList;

public class PythonConcurrencyGraphTest extends PyEnvTestCase {
  public static DrawElement threadStart = new EventDrawElement(null, new StoppedThreadState(), new RunThreadState());
  public static DrawElement threadStop = new EventDrawElement(null, new RunThreadState(), new StoppedThreadState());
  public static DrawElement simple = new SimpleDrawElement(null, new RunThreadState(), new RunThreadState());
  public static DrawElement empty = new SimpleDrawElement(null, new StoppedThreadState(), new StoppedThreadState());
  public static DrawElement event = new EventDrawElement(null, new RunThreadState(), new RunThreadState());
  public static DrawElement lockAcquireStart = new EventDrawElement(null, new RunThreadState(), new LockWaitThreadState());
  public static DrawElement lockAcquired = new EventDrawElement(null, new LockWaitThreadState(), new LockOwnThreadState());
  public static DrawElement lockReleased = new EventDrawElement(null, new LockOwnThreadState(), new RunThreadState());
  public static DrawElement underLock = new EventDrawElement(null, new LockOwnThreadState(), new LockOwnThreadState());


  public static void compareGraphRows(int row, GraphManager graphManager, DrawElement[] correctElements) {
    ArrayList<DrawElement> elements = graphManager.getDrawElementsForRow(row);
    assertEquals(String.format("row = %d", row), correctElements.length, elements.size());
    for (int i = 0; i < elements.size(); ++i) {
      DrawElement graphElement = elements.get(i);
      DrawElement correctElement = correctElements[i];
      assertEquals(String.format("row = %d column = %d", row, i), correctElement, graphElement);
    }
  }

  public static void compareGraphs(GraphManager graphManager, DrawElement[][] correctGraph) {
    for (int i = 0; i < correctGraph.length; ++i) {
      compareGraphRows(i, graphManager, correctGraph[i]);
    }
  }


  public void testThreadMain() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test1.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {threadStop},
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

  public void testThreadCreation() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test2.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {simple, threadStart},
          {simple, simple, threadStart},
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

  public void testThreadJoin() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test3.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();
        DrawElement[][] correct = {
          {threadStart},
          {simple, threadStart},
          {simple, simple, threadStart},
          {event, simple, simple},
          {simple, threadStop, simple},
          {event, empty, simple},
          {simple, empty, threadStop},
          {threadStop, empty, empty}
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

  public void testThreadLockWith() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test4.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {event},
          {simple, threadStart},
          {simple, lockAcquireStart},
          {simple, lockAcquired},
          {simple, lockReleased},
          {simple, simple, threadStart},
          {simple, simple, lockAcquireStart},
          {simple, simple, lockAcquired},
          {simple, simple, lockReleased},
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

  public void testThreadLockAcquireRelease() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test5.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {event},
          {simple, threadStart},
          {simple, lockAcquireStart},
          {simple, lockAcquired},
          {simple, lockReleased},
          {simple, simple, threadStart},
          {simple, simple, lockAcquireStart},
          {simple, simple, lockAcquired},
          {simple, simple, lockReleased},
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

  public void testThreadDoubleLock() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test9.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {event},
          {event},
          {simple, threadStart},
          {simple, lockAcquireStart},
          {simple, lockAcquired},
          {simple, new EventDrawElement(null, new LockOwnThreadState(), new LockWaitThreadState())},
          {simple, new EventDrawElement(null, new LockWaitThreadState(), new LockOwnThreadState())},
          {simple, new EventDrawElement(null, new LockOwnThreadState(), new LockOwnThreadState())},
          {simple, new EventDrawElement(null, new LockOwnThreadState(), new RunThreadState())},
          {threadStop, simple},
        };

        compareGraphs(myGraphManager, correct);

      }
    });
  }

  public void testThreadDeadlock() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test6.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyThreadingLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitFor(myPausedSemaphore, 3000);

        DrawElement[][] correct = {
          {simple, new EventDrawElement(null, new LockOwnThreadState(), new LockWaitThreadState()), underLock},
          {simple, new SimpleDrawElement(null, new LockWaitThreadState(), new DeadlockState()),
            new EventDrawElement(null, new LockOwnThreadState(), new DeadlockState())},
        };

        compareGraphRows(10, myGraphManager, correct[0]);
        compareGraphRows(11, myGraphManager, correct[1]);
      }
    });
  }

  public void testAsyncioTaskCreation() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test7.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyAsyncioLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {simple, threadStart},
          {simple, simple, threadStart},
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

  public void testAsyncioLock() throws Exception {
    runPythonTest(new PyDebuggerTask("/concurrency", "test8.py") {
      public PyConcurrencyLogManager logManager;
      public ConcurrencyColorManager myColorManager;
      public GraphManager myGraphManager;

      @Override
      public void before() throws Exception {
        logManager = PyAsyncioLogManagerImpl.getInstance(getProject());
        myColorManager = new ConcurrencyColorManager();
        myGraphManager = new GraphManager(logManager, myColorManager);
      }

      @Override
      public void testing() throws Exception {
        waitForTerminate();

        DrawElement[][] correct = {
          {threadStart},
          {simple, threadStart},
          {simple, simple, threadStart},
          {lockAcquireStart, simple, simple},
          {lockAcquired, simple, simple},
          {lockReleased, simple, simple},
          {simple, lockAcquireStart, simple},
          {simple, lockAcquired, simple},
          {simple, lockReleased, simple},
        };

        compareGraphs(myGraphManager, correct);
      }
    });
  }

}
