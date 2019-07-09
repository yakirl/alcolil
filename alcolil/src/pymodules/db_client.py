import sys
import os

# print(os.environ["PYTHONPATH"])
print(sys.path)
for path in ['/usr/local/lib/python2.7/dist-packages', "/usr/lib/python2.7", "/usr/lib/python2.7/dist-packages"]:
    sys.path.append(path)
import pymarketstore as pymkts
import numpy as np
import pandas as pd

def is_jep():
    return os.environ.get("INTERP", "jep") == "jep"

if is_jep():
    from java.util import ArrayList

def debug(msg):
    print(msg)


'''
This class uses pymarketstore to access marketstore.
 See https://github.com/alpacahq/marketstore/blob/4811cc6a14a917e97261ce19b223d9b15325037e/frontend/README.md
    for some help docs.
 Bucket consist of: <symbol>/<interval>/<ArttributeGroup>
   interval should be one of the options written here: https://github.com/alpacahq/marketstore/blob/4811cc6a14a917e97261ce19b223d9b15325037e/utils/timeframe.go
   AttributeGroup is the type of data (usually ohlcv or tick but there are no restrictions on this string)

 Data types in the quote DB are documented here: https://github.com/alpacahq/marketstore/blob/4811cc6a14a917e97261ce19b223d9b15325037e/utils/io/numpy.go
'''

INTERVAL_MAP = {
    "ONE_MIN": "1Min",
    "DAILY": "24H",
}

READ_LIMIT = 50

class MarketStoreClient(object):

    def __init__(self):
        self.client = pymkts.Client()
        self.quoteAttributeGroup = "OHLCV"

    def convert(self, interval):
        return INTERVAL_MAP[str(interval)]

    def bucket(self, symbol, interval):
        return '%s/%s/%s' % (symbol, interval, self.quoteAttributeGroup)

    def get_interval_and_symbol(self, barSeries):
        for bar in barSeries:
            interval, symbol = self.convert(bar.interval()), bar.symbol()
            return interval, symbol
            break
        raise Exception("Empty barSeries!")

    def write_to_quote_db(self, barSeries):
        ''' Gets list of Quotes and insert to quotes db
        '''
        interval, symbol = self.get_interval_and_symbol(barSeries)
        debug("Writing to quote DB: symbol %s. interval %s" % (symbol, interval))
        for quote in barSeries:
            timestamp = quote.time().getSeconds()
            self.write(symbol, interval, timestamp, quote.open(), quote.high(), quote.low(), quote.close(), quote.volume())

    def write(self, symbol, interval, timestamp, _open, high, low, close, volume):
        data = np.array([(pd.Timestamp(timestamp).value, _open, high, low, close, volume)],
                        dtype=[('Epoch', 'i8'),('Open', 'f4'),('High', 'f4'), ('Low', 'f4'), ('Close', 'f4'), ('Volume', 'i8')])
        self.client.write(data, self.bucket(symbol, interval))

    def read_from_quote_db(self, symbol, interval):
        replyArr = self.read(symbol, interval).array.tolist()
        retArr = ArrayList(replyArr) if is_jep() else replyArr
        debug("read list: %s" % retArr)
        return retArr

    def print_table(self, symbol, interval):
        table = self.read(symbol, interval).df()
        print(table)

    def read(self, symbol, interval):
        interval = self.convert(interval)
        debug("Reading from quote DB: symbol %s. interval %s" % (symbol, interval))
        param = pymkts.Params(str(symbol), str(interval), self.quoteAttributeGroup, limit=READ_LIMIT)
        return self.client.query(param).first()

    def destroy(self, symbol, interval):
        debug("Destroy quote DB: symbol %s. interval %s" % (symbol, interval))
        self.client.destroy(self.bucket(symbol, interval))

    def clean(self):
        for symbol in self.client.list_symbols():
            for interval in INTERVAL_MAP.values():
                try:
                    self.destroy(symbol, interval)
                except Exception as e:
                    print(e)

    def test_connection(self):
        return self.client.list_symbols()
