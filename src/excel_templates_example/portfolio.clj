(ns excel-templates-example.portfolio
  (:require [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.pprint :refer [cl-format]]
            [excel-templates.build :as excel]))

;;; There are 4 parts to this example:
;;; 1) The description of the portfolio
;;; 2) Code to retrieve price info from Yahoo Finance
;;; 3) Code to format the data as it should go into Excel
;;; 4) Apply the template


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Section 1: Define my portfolio

(def my-portfolio
  [{:symbol "GOOG", :shares 275}
   {:symbol "AAPL", :shares 350}
   {:symbol "MSFT", :shares 2800}
   {:symbol "AMZN", :shares 576}
   {:symbol "AMAT", :shares 4422}
   {:symbol "MON",  :shares 1152}
   {:symbol "XOM",  :shares 750}
   {:symbol "RTN",  :shares 1800}
   {:symbol "WFC",  :shares 900}])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Section 2: Retrieve data from Yahoo Finance

;; Yahoo Finance can be queried with a URL that looks like this:
;; http://ichart.finance.yahoo.com/table.csv?s=MSFT&a=0&b=1&c=2015&d=1&e=31&f=2015&g=d&ignore=.csv
;; Thanks to Joseph Adler's "R in a Nutshell", Chapter 12, for the example.

(defn get-year-month-day
  "Get a seq of year month date from a joda LocalDate"
  [local-date]
  (-> local-date bean ((juxt :year :monthOfYear :dayOfMonth))))

(let [inp-fmt (f/formatters :year-month-day)]
  (defn parse-yahoo-date [date] (f/parse inp-fmt date)))

(defn parse-int
  "Parse an integer from a string"
  [s]
  (Integer/parseInt s))

(defn parse-double
  "Parse a double from a string"
  [s]
  (Double/parseDouble s))

(defn body-as-csv
  "Parse a string as a set of comma-separated-value lines"
  [s]
  (let [lines (.split s "\n")]
    (for [line lines]
      (.split line ","))))

(defn convert-items
  "In a seq of seqs, convert the items in each row with the functions supplied, one
   function for each column."
  [data conversions]
  (for [row data]
    (map #(%2 %1) row conversions)))

(defn stock-to-map
  "Convert the rows returned from Yahoo Finance to maps"
  [rows sym]
  (for [row rows]
    (-> (zipmap [:date :open :high :low :close :volume :adj-close] row)
        (assoc :symbol sym))))

(defn get-yahoo-stock-data
  "Get the data for a certain period from yahoo finance. Returns a seq of maps
   with the keys [:symbol :date :open :high :low :close :volume :adj-close]"
  [sym start end interval]
  (let [[sy sm sd] (get-year-month-day start)
        [ey em ed] (get-year-month-day end)
        url (cl-format nil "http://ichart.finance.yahoo.com/table.csv?s=~a&a=~d&b=~d&c=~d&d=~d&e=~d&f=~d&g=~a&ignore=.csv"
                       sym (dec sm) sd sy (dec em) ed ey interval)
        sdata (-> url client/get :body)]
    (-> sdata
        body-as-csv
        rest
        (convert-items [parse-yahoo-date parse-double parse-double parse-double
                        parse-double parse-int parse-double])
        (stock-to-map sym))))

(defn get-portfolio-status
  "Get the share data for our portfolio for the past two market days"
  [portfolio]
  (let [end (org.joda.time.LocalDate.)
        start (.minusDays end 7)]
    (for [{:keys [symbol shares]} portfolio
          :let [[day2 day1] (get-yahoo-stock-data symbol start end "d")]]
      {:symbol symbol :shares shares :day1 day1 :day2 day2})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Section 3: Organize output to be the way we want it in
;;;            Excel

(defn holdings-rows
  "Get the share data for our portfolio for the past two market days"
  [holdings]
  (let [[day1 day2] (->> holdings first ((juxt :day1 :day2)) (map :date))]
    {:day1 day1
     :day2 day2
     :rows (for [{:keys [symbol shares day1 day2]} holdings]
             [symbol shares nil
              (:open day1) (:low day1) (:high day1) (:close day1) (* (:close day1) shares)
              nil
              (:open day2) (:low day2) (:high day2) (:close day2) (* (:close day2) shares)
              nil
              (- (:close day2) (:close day1))
              (* shares (- (:close day2) (:close day1)))
              (/ (- (:close day2) (:close day1)) (:close day1))])}))

(defn create-row-data
  "Massage the data into the form for the template"
  [holdings]
  (let [{:keys [day1 day2 rows]} (holdings-rows holdings)
        title (format "Portfolio Status as of %s"
                      (f/unparse (f/formatter "MMMM d, y") day2))
        section-row [nil nil nil (f/unparse (f/formatter "EEEE, M/d") day1)
                     nil nil nil nil nil (f/unparse (f/formatter "EEEE, M/d") day2)]
        total-row (let [day1-total (reduce + (map #(nth % 7) rows))
                        day2-total (reduce + (map #(nth % 13) rows))
                        change (- day2-total day1-total)
                        frac-change (/ change day1-total)]
                    [nil nil nil nil nil nil nil
                     day1-total
                     nil nil nil nil nil
                     day2-total
                     nil nil
                     change frac-change])]
    {"Portfolio" {0 [[title]]
                  2 [section-row]
                  4 rows
                  5 [total-row]}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Section 4: Apply Template

(defn apply-template
  "Apply the Excel template to the generated rows"
  [row-data]
  (excel/render-to-file
   "portfolio-template.xlsx"
   "/tmp/portfolio.xlsx"
   row-data))

(defn excel-portfolio-report
  "Make an excel report for our portfolio"
  []
  (-> my-portfolio
      get-portfolio-status
      create-row-data
      apply-template))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Extra Section: Make a csv


(defn make-simple-rows
  "Make the basic rows from the holdings data"
  [holdings]
  (for [{:keys [symbol shares day1 day2]} holdings]
    [symbol shares
     (:open day1) (:low day1) (:high day1) (:close day1) (* (:close day1) shares)
     (:open day2) (:low day2) (:high day2) (:close day2) (* (:close day2) shares)
     (- (:close day2) (:close day1))
     (* shares (- (:close day2) (:close day1)))
     (/ (- (:close day2) (:close day1)) (:close day1))]))

(defn to-csv
  "Export a series of rows as CSV"
  [rows]
  (let [headers [["" ""
                  "Day 1" "" "" "" ""
                  "Day 2" "" "" "" ""
                  "Change" "" ""]
                 ["Stock" "Shares"
                  "Open" "Low" "High" "Close" "Holdings"
                  "Open" "Low" "High" "Close" "Holdings"
                  "Share Price" "Total Value" "Percentage"]]]
    (spit "/tmp/portfolio.csv" (cl-format nil "~{~{~a~^,~}~%~}" (concat headers rows)))))
