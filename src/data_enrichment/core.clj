(ns data-enrichment.core
  (:require [data-enrichment.crawlers.snapdeal.impl :as sd]
            [data-enrichment.crawlers.amazon.impl :as amzn]
            [data-enrichment.string.utils :refer [levenshtein-distance]]
            [taoensso.timbre :as log]
            [cheshire.core :as json])
  (:gen-class))

(defn enrich-with-reviews
  "Enriches snapdeal's data using amazon's reviews of the same products"
  [categ-number]
  (let [sd-data (sd/product-details (sd/category-urls categ-number))]
    (remove
      (fn [d] (empty? (:reviews d))) ; Removing products wwith no reviews
      (map
        (fn [d]
          (let [searched-product (try
                                   (amzn/query-and-get-product-details (:product-title d))
                                   (catch Exception e []))]
            ; Removing products whose metadata is not present or whose names do not match
            (if (and (not-empty searched-product)
                     (>= (levenshtein-distance (:product-title d) (:product-title searched-product)) 65))
              (assoc d :reviews
                (->> (get searched-product :product-url)
                     (amzn/product-review-page "positive")
                     (amzn/product-reviews)))
              (assoc d :reviews []))))
       sd-data))))

(defn -main
  "Takes in one argument, a number from 1 to 5, which corresponds to a category, Mappings given below:
   1 -> Books
   2 -> TVs
   3 -> Desktop Computers
   4 -> Furniture
   5 -> Smartphones"
  [& args]
  (log/info "Working...")
  (try
    (spit
      "output-data.json"
      (json/generate-string
        (enrich-with-reviews (first args))))
    (catch Exception e
      (log/error e "Oh no!"))
    (finally
      (log/info "Finished :)"))))
