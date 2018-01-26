(ns data-enrichment.crawlers.amazon.impl
  (:require [data-enrichment.crawlers.general :refer [parse-html-body]]
            [clojure.string :as string]))

(defn search-url-generator
  "Generates an 'amazon.in' search URL for the query specified.
   Query can be any text"
  [query]
  (let [base-url "https://www.amazon.in/s/ref=nb_sb_noss?url=search-alias=aps&field-keywords="]
    (str base-url (string/replace query #"\s+" "+"))))

(defn product-list
  "Scraps the HTML of products list"
  [url]
  (-> (parse-html-body url)
      (.getElementById "centerMinus")
      (.select "li")))

(defn extract-product-details
  "Extracts product's details from the 'li' (List Element) HTML tag"
  [li-tag-elem]
  (-> li-tag-elem
      (.select "a[class=a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal]")
      .first))

(defn product-details
  "Gets the product details (URL and Title)"
  [product]
  (let [extracted-product (extract-product-details product)]
    {:product-url (-> (first (string/split (.attr extracted-product "href") #"&keywords="))
                      (string/split #"/ref=")
                      first)
     :product-title (.attr extracted-product "title")}))

(defn query-and-get-product-details
  "Gets product details by searching for query on it's search page"
  [query]
  (let [p-list (product-list (search-url-generator query))]
    ; Selecting first product search match as it is highly likely
    ; that it is the product that has been searched
    ; NOTE: Putting blind faith in Amazon's search API
    (product-details (first p-list))))

(defn product-review-page
  "Get the review page URL of a product
   NOTE: The argument 'star' can take the following values:
   [five_star, four_star, three_star, two_star, one_star, positive, critical]"
  [star product-url]
  (str (string/replace product-url #"/dp/" "/product-reviews/")
       "?filterByStar=" star "&reviewerType=all_reviews&pageNumber=1"))

(defn product-reviews
  "Gets the reviews of a product from the reviews page"
  [product-reviews-url]
  (let [reviews-raw-data (.select
                           (parse-html-body product-reviews-url)
                           "div[class=a-section review]")]
    (->> (map
           (fn [r]
             {:stars (let [stars-string (-> (.getElementsByClass r "a-link-normal") .first
                                            (.attr "title"))
                           stars-split (string/split stars-string #"\sout\sof\s")
                           numerator (read-string (first stars-split))
                           denominator (read-string (first (string/split (second stars-split) #"\s")))]
                       [numerator denominator])
              :title (-> (.select r "a[class=a-size-base a-link-normal review-title a-color-base a-text-bold]")
                         .first
                         .text)
              :comments (-> (.select r "div[class=a-row review-data]") .first
                            (.select "span") .first
                            .text)})
         reviews-raw-data)
         (filter
           (fn [{:keys [stars & remaining] :as r}]
             (>= (first stars) 3))) ; Keeping only positive reviews
         (map
           (fn [{:keys [stars & remaining] :as r}]
             (assoc r :stars
               (str (first stars) " out of " (second stars) " stars")))))))
