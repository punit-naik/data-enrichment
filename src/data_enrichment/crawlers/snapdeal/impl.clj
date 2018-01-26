(ns data-enrichment.crawlers.snapdeal.impl
  (:require [data-enrichment.crawlers.general :refer [parse-html-body]]))

; Sorting by Popularity by default
(defonce category-urls
  {"1" "https://www.snapdeal.com/products/books?sort=plrty"
   "2" "https://www.snapdeal.com/products/electronic-tv-accessories?sort=plrty"
   "3" "https://www.snapdeal.com/products/computers-desktops?sort=plrty"
   "4" "https://www.snapdeal.com/products/furniture?sort=plrty"
   "5" "https://www.snapdeal.com/products/mobiles-mobile-phones/filters/Form_s~Smartphones?sort=plrty"})

(defn product-details
  "Gets the product details from the products search results page of snapdeal.com
  (Product URL, Title and Picture URL)"
  [url]
  (let [sections (.getElementsByTag
                   (parse-html-body url)
                   "section")]
    (flatten
      (pmap
        (fn [x]
          (let [product (.select x "div[class=col-xs-6  favDp product-tuple-listing js-tuple]")]
            (pmap
              (fn [y]
                (let [product-url (-> (.select y "div[class=product-tuple-image]") .first
                                      (.select "a") .first)
                      product-picture-and-title (-> product-url
                                                    (.select "picture") .first
                                                    (.select "source") .first)]
                  {:product-url (.attr product-url "href")
                   :product-picture (.attr product-picture-and-title "srcset")
                   :product-title (.attr product-picture-and-title "title")}))
              product)))
        sections))))
