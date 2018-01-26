(ns data-enrichment.crawlers.general
  (:import [org.jsoup Jsoup]))

(defn parse-html-body
  "Hits the specified URL and parses its HTML body into type 'org.jsoup.nodes.Element'"
  [url]
  (-> (Jsoup/connect url)
      (.userAgent "Mozilla")
      (.timeout 10000)
      .get .body))
