from threading import Thread

def f(number):
    print('got number {}'.format(number))

threads = [Thread(target=f, args=(i,)) for i in range(1, 3)]

for thread in threads:
    thread.start()