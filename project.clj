(defproject excel-templates-example "0.2.0-SNAPSHOT"
  :description "An example of using an Excel template to build a report."
  :url "https://github.com/tomfaulhaber/excel-templates-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "2.1.0"]
                 [clj-time "0.9.0"]
                 [com.infolace/excel-templates "0.3.3"]
                 [org.clojure/clojure "1.8.0"]]
  :main excel-templates-example.portfolio)
