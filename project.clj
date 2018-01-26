(defproject data-enrichment "0.1.0-SNAPSHOT"
  :description "Enriches snapdeal.com's products' data by appending review metadata from amazon.in"
  :url "https://github.com/punit-naik/data-enrichment"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v20.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ; Crawling
                 [org.jsoup/jsoup "1.8.3"]
                 ; Logging
                 [com.taoensso/timbre "4.10.0"]
                 ; JSON
                 [cheshire "5.8.0"]]
  :plugins [[lein-kibit "0.1.5"]
            [jonase/eastwood "0.2.5"]
            [lein-cloverage "1.0.10"]
            [lein-codox "0.10.3"]]
  :profiles {:uberjar {:aot :all}}
  :codox {:output-path "doc"}
  :main data-enrichment.core)
