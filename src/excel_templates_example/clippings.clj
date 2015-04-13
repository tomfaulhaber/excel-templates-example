(ns excel-templates-example.clippings)


(defn history-per-stock
  "Build the sheets for the YTD history of each stock in the portfolio"
  [holdings]
  (for [{:keys [symbol name shares history]} holdings]
    {:sheet-name symbol
     0 [[(str "YTD Info for " name)]]
     1 [[symbol nil shares]]
     [4 5] (for [{:keys [date open close]} (reverse history)]
             [date open close])}))

(defn create-row-data
  "Massage the data into the form for the template"
  [holdings]
  {"Portfolio" (last-two-days holdings)
   "PerStock" (history-per-stock holdings)})
