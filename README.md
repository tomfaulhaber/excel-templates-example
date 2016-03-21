# An Example Project for excel-templates

This project uses the [excel-templates](https://github.com/tomfaulhaber/excel-templates) library to create a pretty spreadsheet of a (fictional) stock portfolio by pulling live data from Yahoo Finance.

It is intended as a usage example for the [excel-templates](https://github.com/tomfaulhaber/excel-templates) library.

## Usage

``` clj
(use '[excel-templates-example/portfolio])

(-> my-portfolio
    get-portfolio-status
    create-row-data
    apply-template)
```

## License

Copyright © 2015-6 Tom Faulhaber

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
