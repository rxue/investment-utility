# Java SE
## `ArithmeticException` caused by `divide` operation in `BigDecimal`
NOTE that the `BigDecimal.divide(BigDecimal divisor)`, say without rounding mode nor scale might have "non-terminating decimal expansion". This is related to the real math problem

# Practical Tips
## 20261022 Concurrency issue on `LegacyConcurrentQuoteFetcher`
- issue 1: separate http client used in each thread
    - It undermines the point of concurrent mode. Up to 10 threads each fetch a cookie and a crumb before fetching any quotes.
    - It's the most likely to cause real failures. Bursts of crumb requests are what Yahoo tends to rate-limit (HTTP 429). One rejected crumb fails the whole batch, and because of issue 2 you can't tell which ticker hit it.
- Issue 2: the executor that is never shut down, comes second. It's a real bug, but it's hidden right now: the System.exit in Main ends the JVM regardless. It only becomes a hang if that exit is removed or the fetcher is used outside the CLI
## JUnit 5
### `fail` 20261117
# Yahoo Finance API
## `https://query1.finance.yahoo.com/v8/finance/chart`
sample data

```
{
  "chart" : {
    "result" : [ {
      "meta" : {
        "currency" : "USD",
        "symbol" : "PFE",
        "exchangeName" : "NYQ",
        "fullExchangeName" : "NYSE",
        "instrumentType" : "EQUITY",
        "firstTradeDate" : 76253400,
        "regularMarketTime" : 1794862898,
        "hasPrePostMarketData" : true,
        "gmtoffset" : -18000,
        "timezone" : "EST",
        "exchangeTimezoneName" : "America/New_York",
        "regularMarketPrice" : 28.86,
        "regularMarketChangePercent" : -0.104,
        "fulldayPrice" : 28.82,
        "fulldayChange" : -0.07,
        "fulldayChangePercent" : -0.242,
        "fiftyTwoWeekHigh" : 31.44,
        "fiftyTwoWeekLow" : 23.62,
        "regularMarketDayHigh" : 29.28,
        "regularMarketDayLow" : 28.81,
        "regularMarketVolume" : 30818817,
        "longName" : "Pfizer Inc.",
        "shortName" : "Pfizer, Inc.",
        "chartPreviousClose" : 24.9,
        "priceHint" : 2,
        "currentTradingPeriod" : {
          "pre" : {
            "timezone" : "EST",
            "end" : 1794925800,
            "start" : 1794906000,
            "gmtoffset" : -18000
          },
          "regular" : {
            "timezone" : "EST",
            "end" : 1794949200,
            "start" : 1794925800,
            "gmtoffset" : -18000
          },
          "post" : {
            "timezone" : "EST",
            "end" : 1794963600,
            "start" : 1794949200,
            "gmtoffset" : -18000
          }
        },
        "dataGranularity" : "1d",
        "range" : "",
        "validRanges" : [ "1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "ytd", "max" ]
      },
      "timestamp" : [ 1767364200, 1767623400 ],
      "indicators" : {
        "quote" : [ {
          "open" : [ 24.979999542236328, 25.200000762939453 ],
          "volume" : [ 35976700, 52620900 ],
          "low" : [ 24.829999923706055, 24.950000762939453 ],
          "high" : [ 25.299999237060547, 25.399999618530273 ],
          "close" : [ 25.18000030517578, 25.170000076293945 ]
        } ],
        "adjclose" : [ {
          "adjclose" : [ 23.599550247192383, 23.59017562866211 ]
        } ]
      }
    } ],
    "error" : null
  }
}
```
