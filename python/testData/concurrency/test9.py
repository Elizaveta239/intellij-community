import threading

lock1 = threading.Lock()
lock2 = threading.Lock()

class MyThread (threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)

    def run(self):
        lock1.acquire()
        lock2.acquire()
        lock2.release()
        lock1.release()


thread = MyThread()
thread.start()