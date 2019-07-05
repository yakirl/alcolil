import os
os.environ["INTERP"] = "python"
import db_client

class Time(object):
    def __init__(self, timestamp):
        self.timestamp = timestamp

    def getSeconds(self):
        return self.timestamp

class Quote(object):

    def __init__(self, symbol, _open, high, low, close, volume, interval, time):
        self._interval = interval
        self._symbol = symbol
        self._open = _open
        self._high = high
        self._low = low
        self._close = close
        self._volume = volume
        self._time = Time(time)

    def interval(self):
        return self._interval

    def symbol(self):
        return self._symbol

    def open(self):
        return self._open

    def high(self):
        return self._high

    def low(self):
        return self._low

    def close(self):
        return self._close

    def volume(self):
        return self._volume

    def time(self):
        return self._time

class MarketStoreClientTest(object):

    def test(self):
        self.test_read_write_simple()

    def test_read_write_simple(self):
        c = db_client.MarketStoreClient()
        symbol = "INTC"
        interval = "ONE_MIN"
        data = [
            (1.2, 3.2, 4.3, 2.2, 1210, 11913323233543L),
            (5.4, 22.2, 1.1, 4.1, 12122, 11223778233543L),
            (55, 3.2, 6.7, 11, 442, 17655533543898L),
            (9.9, 8.8, 7.7, 9, 444, 17713239546453L),
        ]
        barSeries = [Quote(symbol, _open, high, low, close, volume, interval, time) for _open, high, low, close, volume, time in data]
        c.write_to_quote_db(barSeries)
        reply = c.read_from_quote_db(symbol, interval)
        print(reply)

    def read(self):
        c = db_client.MarketStoreClient()
        symbol = "INTC"
        interval = "ONE_MIN"
        reply = c.read_from_quote_db(symbol, interval)
        print(reply)

test = MarketStoreClientTest()
test.test()
