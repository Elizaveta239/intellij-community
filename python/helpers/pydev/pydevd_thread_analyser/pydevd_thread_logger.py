
from pydevd_constants import DictContains, GetThreadId
from pydevd_file_utils import GetFilenameAndBase
from pydevd_thread_analyser.pydevd_thread_wrappers import LockWrapper
import pydevd_vars
import time

import _pydev_threading as threading
threadingCurrentThread = threading.currentThread


DONT_TRACE_THREADING = ['threading.py', 'pydevd.py']
THREAD_METHODS = ['start', '_stop']
INNER_METHODS = ['_stop']
INNER_FILES = ['threading.py']
LOCK_METHODS = ['acquire', 'release']
LOCK_CONTEXT_MGR = ['__enter__', '__exit__']

from pydevd_comm import GlobalDebuggerHolder, NetCommand
import traceback

import time
cur_time = lambda: int(round(time.time() * 10000))

class ThreadingLogger:
    def __init__(self):
        self.start_time = cur_time()

    def send_message(self, time, name, thread_id, type, event, file, line):
        dbg = GlobalDebuggerHolder.globalDbg
        cmdTextList = ['<xml>']

        cmdTextList.append('<threading_event')
        cmdTextList.append(' time="%s"' % pydevd_vars.makeValidXmlValue(str(time)))
        cmdTextList.append(' name="%s"' % pydevd_vars.makeValidXmlValue(name))
        cmdTextList.append(' thread_id="%s"' % pydevd_vars.makeValidXmlValue(thread_id))
        cmdTextList.append(' type="%s"' % pydevd_vars.makeValidXmlValue(type))
        cmdTextList.append(' event="%s"' % pydevd_vars.makeValidXmlValue(event))
        cmdTextList.append(' file="%s"' % pydevd_vars.makeValidXmlValue(file))
        cmdTextList.append(' line="%s"' % pydevd_vars.makeValidXmlValue(str(line)))
        cmdTextList.append('></threading_event></xml>')

        text = ''.join(cmdTextList)
        dbg.writer.addCommand(NetCommand(144, 0, text))

    def log_event(self, frame):
        write_log = False
        self_obj = None
        if DictContains(frame.f_locals, "self"):
            self_obj = frame.f_locals["self"]
            if isinstance(self_obj, threading.Thread) or self_obj.__class__ == LockWrapper:
                write_log = True

        try:
            if write_log:
                t = threadingCurrentThread()
                back = frame.f_back
                if not back:
                    return
                name, back_base = GetFilenameAndBase(back)
                event_time = cur_time() - self.start_time
                file_name = frame.f_code.co_name
                if back_base not in DONT_TRACE_THREADING or (file_name in INNER_METHODS and back_base in INNER_FILES):
                    if isinstance(self_obj, threading.Thread) and file_name in THREAD_METHODS:
                        thread_id = GetThreadId(self_obj)
                        method_name = frame.f_code.co_name
                        if method_name == "_stop":
                            # TODO: Python 2
                            if back_base in INNER_FILES and back.f_code.co_name == "_wait_for_tstate_lock":
                                method_name = "join"
                            else:
                                method_name = "stop"
                        self.send_message(event_time, self_obj.getName(), thread_id, "thread", method_name, back.f_code.co_filename, back.f_lineno)
                        # print(event_time, self_obj.getName(), thread_id, back_base, back.f_lineno, frame.f_code.co_name)
                    elif self_obj.__class__ == LockWrapper:
                        if DictContains(frame.f_locals, "attr") and frame.f_locals["attr"] in LOCK_METHODS:
                            thread_id = GetThreadId(self_obj)
                            method_name = frame.f_locals["attr"]
                            self.send_message(event_time, t.getName(), GetThreadId(t), "lock", method_name, back.f_code.co_filename, back.f_lineno)
                            # print(event_time, t.getName(), GetThreadId(t), back_base, back.f_lineno, frame.f_locals["attr"])
                        elif file_name in LOCK_CONTEXT_MGR:
                            thread_id = GetThreadId(self_obj)
                            method_name = frame.f_code.co_name
                            self.send_message(event_time, t.getName(), GetThreadId(t), "lock", method_name, back.f_code.co_filename, back.f_lineno)
                            # print(event_time, t.getName(), GetThreadId(t), back_base, back.f_lineno, frame.f_code.co_name)
        except e:
            traceback.print_exc(e)