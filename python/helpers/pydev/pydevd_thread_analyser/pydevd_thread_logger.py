
from pydevd_constants import DictContains, GetThreadId
from pydevd_file_utils import GetFilenameAndBase
from pydevd_thread_analyser.pydevd_thread_wrappers import LockWrapper
import pydevd_vars
import time

import _pydev_threading as threading
threadingCurrentThread = threading.currentThread


DONT_TRACE_THREADING = ['threading.py', 'pydevd.py']
INNER_METHODS = ['_stop']
INNER_FILES = ['threading.py']
THREAD_METHODS = ['start', '_stop']
LOCK_METHODS = ['acquire', 'release', '__enter__', '__exit__']

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
                method_name = frame.f_code.co_name

                if isinstance(self_obj, threading.Thread) and method_name in THREAD_METHODS:
                    if back_base not in DONT_TRACE_THREADING or \
                            (method_name in INNER_METHODS and back_base in INNER_FILES):
                        thread_id = GetThreadId(self_obj)
                        real_method = frame.f_code.co_name
                        if real_method == "_stop":
                            # TODO: Python 2
                            if back_base in INNER_FILES and \
                                            back.f_code.co_name == "_wait_for_tstate_lock":
                                real_method = "join"
                                back = back.f_back.f_back
                            else:
                                real_method = "stop"
                        self.send_message(event_time, self_obj.getName(), thread_id, "thread",
                        real_method, back.f_code.co_filename, back.f_lineno)
                        # print(event_time, self_obj.getName(), thread_id, "thread",
                        #       real_method, back.f_code.co_filename, back.f_lineno)

                if self_obj.__class__ == LockWrapper:
                    if back_base in DONT_TRACE_THREADING:
                        # do not trace methods called from threading
                        return
                    if DictContains(frame.f_locals, "attr") and \
                                    frame.f_locals["attr"] in LOCK_METHODS:
                        _, back_back_base = GetFilenameAndBase(back.f_back)
                        back = back.f_back
                        if back_back_base in DONT_TRACE_THREADING:
                            # back_back_base is the file, where the method was called froms
                            return
                        real_method = frame.f_locals["attr"]
                        if method_name == "call_begin":
                            real_method += "_begin"
                        elif method_name == "call_end":
                            real_method += "_end"
                        else:
                            return
                        if real_method == "release_end":
                            # do not log release end. Maybe use it later
                            return
                        self.send_message(event_time, t.getName(), GetThreadId(t), "lock",
                        real_method, back.f_code.co_filename, back.f_lineno)
                        # print(event_time, t.getName(), GetThreadId(t), "lock",
                        #       real_method, back.f_code.co_filename, back.f_lineno)

        except Exception:
            traceback.print_exc()