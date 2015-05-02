
from pydevd_constants import DictContains, GetThreadId
import pydevd_file_utils
from pydevd_concurrency_analyser.pydevd_thread_wrappers import LockWrapper
import pydevd_vars
import time

from _pydev_filesystem_encoding import getfilesystemencoding
file_system_encoding = getfilesystemencoding()

try:
    from urllib import quote
except:
    from urllib.parse import quote

import _pydev_threading as threading
threadingCurrentThread = threading.currentThread


DONT_TRACE_THREADING = ['threading.py', 'pydevd.py']
INNER_METHODS = ['_stop']
INNER_FILES = ['threading.py']
THREAD_METHODS = ['start', '_stop', 'join']
LOCK_METHODS = ['acquire', 'release', '__enter__', '__exit__']

from pydevd_comm import GlobalDebuggerHolder, NetCommand
import traceback

import time
cur_time = lambda: int(round(time.time() * 10000))


try:
    import asyncio
except:
    pass


def get_text_list_for_frame(frame):
    # partial copy-paste from makeThreadSuspendStr
    curFrame = frame
    cmdTextList = []
    try:
        while curFrame:
            #print cmdText
            myId = str(id(curFrame))
            #print "id is ", myId

            if curFrame.f_code is None:
                break #Iron Python sometimes does not have it!

            myName = curFrame.f_code.co_name #method name (if in method) or ? if global
            if myName is None:
                break #Iron Python sometimes does not have it!

            #print "name is ", myName

            filename, base = pydevd_file_utils.GetFilenameAndBase(curFrame)

            myFile = pydevd_file_utils.NormFileToClient(filename)
            if file_system_encoding.lower() != "utf-8" and hasattr(myFile, "decode"):
                # myFile is a byte string encoded using the file system encoding
                # convert it to utf8
                myFile = myFile.decode(file_system_encoding).encode("utf-8")

            #print "file is ", myFile
            #myFile = inspect.getsourcefile(curFrame) or inspect.getfile(frame)

            myLine = str(curFrame.f_lineno)
            #print "line is ", myLine

            #the variables are all gotten 'on-demand'
            #variables = pydevd_vars.frameVarsToXML(curFrame.f_locals)

            variables = ''
            cmdTextList.append('<frame id="%s" name="%s" ' % (myId , pydevd_vars.makeValidXmlValue(myName)))
            cmdTextList.append('file="%s" line="%s">"' % (quote(myFile, '/>_= \t'), myLine))
            cmdTextList.append(variables)
            cmdTextList.append("</frame>")
            curFrame = curFrame.f_back
    except :
        traceback.print_exc()

    return cmdTextList




def send_message(event_class, time, name, thread_id, type, event, file, line, frame, lock_id=0, parent=None):
    dbg = GlobalDebuggerHolder.globalDbg
    cmdTextList = ['<xml>']

    cmdTextList.append('<' + event_class)
    cmdTextList.append(' time="%s"' % pydevd_vars.makeValidXmlValue(str(time)))
    cmdTextList.append(' name="%s"' % pydevd_vars.makeValidXmlValue(name))
    cmdTextList.append(' thread_id="%s"' % pydevd_vars.makeValidXmlValue(thread_id))
    cmdTextList.append(' type="%s"' % pydevd_vars.makeValidXmlValue(type))
    if type == "lock":
        cmdTextList.append(' lock_id="%s"' % pydevd_vars.makeValidXmlValue(str(lock_id)))
    if parent is not None:
        cmdTextList.append(' parent="%s"' % pydevd_vars.makeValidXmlValue(parent))
    cmdTextList.append(' event="%s"' % pydevd_vars.makeValidXmlValue(event))
    cmdTextList.append(' file="%s"' % pydevd_vars.makeValidXmlValue(file))
    cmdTextList.append(' line="%s"' % pydevd_vars.makeValidXmlValue(str(line)))
    cmdTextList.append('></' + event_class + '>')

    cmdTextList += get_text_list_for_frame(frame)
    cmdTextList.append('</xml>')

    text = ''.join(cmdTextList)
    dbg.writer.addCommand(NetCommand(144, 0, text))


class ThreadingLogger:
    def __init__(self):
        self.start_time = cur_time()

    def send_message(self, time, name, thread_id, type, event, file, line, frame, lock_id=0, parent=None):
        dbg = GlobalDebuggerHolder.globalDbg
        cmdTextList = ['<xml>']

        cmdTextList.append('<threading_event')
        cmdTextList.append(' time="%s"' % pydevd_vars.makeValidXmlValue(str(time)))
        cmdTextList.append(' name="%s"' % pydevd_vars.makeValidXmlValue(name))
        cmdTextList.append(' thread_id="%s"' % pydevd_vars.makeValidXmlValue(thread_id))
        cmdTextList.append(' type="%s"' % pydevd_vars.makeValidXmlValue(type))
        if type == "lock":
            cmdTextList.append(' lock_id="%s"' % pydevd_vars.makeValidXmlValue(str(lock_id)))
        if parent is not None:
            cmdTextList.append(' parent="%s"' % pydevd_vars.makeValidXmlValue(parent))
        cmdTextList.append(' event="%s"' % pydevd_vars.makeValidXmlValue(event))
        cmdTextList.append(' file="%s"' % pydevd_vars.makeValidXmlValue(file))
        cmdTextList.append(' line="%s"' % pydevd_vars.makeValidXmlValue(str(line)))
        cmdTextList.append('></threading_event>')

        cmdTextList += get_text_list_for_frame(frame)
        cmdTextList.append('</xml>')

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
                name, back_base = pydevd_file_utils.GetFilenameAndBase(back)
                event_time = cur_time() - self.start_time
                method_name = frame.f_code.co_name

                if isinstance(self_obj, threading.Thread) and method_name in THREAD_METHODS:
                    if back_base not in DONT_TRACE_THREADING or \
                            (method_name in INNER_METHODS and back_base in INNER_FILES):
                        thread_id = GetThreadId(self_obj)
                        name = self_obj.getName()
                        real_method = frame.f_code.co_name
                        if real_method == "_stop":
                            # TODO: Python 2
                            if back_base in INNER_FILES and \
                                            back.f_code.co_name == "_wait_for_tstate_lock":
                                back = back.f_back.f_back
                            real_method = "stop"
                        elif real_method == "join":
                            # join called in the current thread, not in self object
                            thread_id = GetThreadId(t)
                            name = t.getName()

                        parent = None
                        if real_method in ("start", "stop"):
                            parent = GetThreadId(t)
                        send_message("threading_event", event_time, name, thread_id, "thread",
                        real_method, back.f_code.co_filename, back.f_lineno, back, parent=parent)
                        # print(event_time, self_obj.getName(), thread_id, "thread",
                        #       real_method, back.f_code.co_filename, back.f_lineno)

                if self_obj.__class__ == LockWrapper:
                    if back_base in DONT_TRACE_THREADING:
                        # do not trace methods called from threading
                        return
                    if DictContains(frame.f_locals, "attr") and \
                                    frame.f_locals["attr"] in LOCK_METHODS:
                        _, back_back_base = pydevd_file_utils.GetFilenameAndBase(back.f_back)
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
                        send_message("threading_event", event_time, t.getName(), GetThreadId(t), "lock",
                        real_method, back.f_code.co_filename, back.f_lineno, back, lock_id=str(id(self_obj)))
                        # print(event_time, t.getName(), GetThreadId(t), "lock",
                        #       real_method, back.f_code.co_filename, back.f_lineno)

        except Exception:
            traceback.print_exc()


class NameManager():
    def __init__(self, name_prefix):
        self.tasks = {}
        self.last = 0
        self.prefix = name_prefix

    def get(self, id):
        if id not in self.tasks:
            self.last += 1
            self.tasks[id] = self.prefix + "-" + str(self.last)
        return self.tasks[id]


class AsyncioLogger:
    def __init__(self):
        self.task_mgr = NameManager("Task")
        self.coro_mgr = NameManager("Coro")
        self.start_time = cur_time()

    def get_task_id(self, frame):
        while frame is not None:
            if DictContains(frame.f_locals, "self"):
                self_obj = frame.f_locals["self"]
                if isinstance(self_obj,  asyncio.Task):
                    method_name = frame.f_code.co_name
                    if method_name == "_step":
                        return id(self_obj)
            frame = frame.f_back
        return None

    def log_event(self, frame):
        self_obj = None
        event_time = event_time = cur_time() - self.start_time


        if DictContains(frame.f_locals, "self"):
            self_obj = frame.f_locals["self"]

        method_name = frame.f_code.co_name
        if isinstance(self_obj, asyncio.base_events.BaseEventLoop):
            if method_name == "_run_once":
                print("Loop iteration")

        if not hasattr(frame, "f_back") or frame.f_back is None:
            return

        back = frame.f_back
        method_name = back.f_code.co_name

        if DictContains(frame.f_locals, "self"):
            self_obj = frame.f_locals["self"]
            if isinstance(self_obj, asyncio.Task):
                if method_name in ("__init__",):
                    task_id = id(self_obj)
                    task_name = self.task_mgr.get(str(task_id))
                    send_message("asyncio_event", event_time, task_name, task_name, "thread", "start", frame.f_code.co_filename,
                                 frame.f_lineno, frame)

            method_name = frame.f_code.co_name
            if isinstance(self_obj, asyncio.Lock):
                if method_name in ("acquire", "release"):
                    task_id = self.get_task_id(frame)
                    task_name = self.task_mgr.get(str(task_id))

                    if method_name == "acquire":
                        if not self_obj._waiters and not self_obj.locked():
                            send_message("asyncio_event", event_time, task_name, task_name, "lock",
                                         method_name+"_begin", frame.f_code.co_filename, frame.f_lineno, frame, lock_id=str(id(self_obj)))
                        if self_obj.locked():
                            method_name += "_begin"
                        else:
                            method_name += "_end"
                    else:
                        method_name += "_end"

                    send_message("asyncio_event", event_time, task_name, task_name, "lock",
                                 method_name, frame.f_code.co_filename, frame.f_lineno, frame, lock_id=str(id(self_obj)))
