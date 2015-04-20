#!/bin/sh

mkdir -p saved-portfolio
cd saved-portfolio

curl --output AAPL.csv 'http://ichart.finance.yahoo.com/table.csv?s=AAPL&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output AKAM.csv 'http://ichart.finance.yahoo.com/table.csv?s=AKAM&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output AMZN.csv 'http://ichart.finance.yahoo.com/table.csv?s=AMZN&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output IBM.csv 'http://ichart.finance.yahoo.com/table.csv?s=IBM&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output MON.csv 'http://ichart.finance.yahoo.com/table.csv?s=MON&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output NFLX.csv 'http://ichart.finance.yahoo.com/table.csv?s=NFLX&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output SPLS.csv 'http://ichart.finance.yahoo.com/table.csv?s=SPLS&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output TDC.csv 'http://ichart.finance.yahoo.com/table.csv?s=TDC&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
curl --output WMT.csv 'http://ichart.finance.yahoo.com/table.csv?s=WMT&a=0&b=1&c=2015&d=3&e=19&f=2015&g=d&ignore=.csv'
