import asyncio

@asyncio.coroutine
def factorial(name, number):
    print("Task %s: number = %s" % (name, number))

loop = asyncio.get_event_loop()
tasks = [
    asyncio.async(factorial("A", 2)),
    asyncio.async(factorial("B", 3))]
loop.run_until_complete(asyncio.wait(tasks))
loop.close()