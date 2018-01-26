(ns data-enrichment.core-test
  (:require [clojure.test :refer [is deftest testing]]
            [data-enrichment.crawlers.amazon.impl :refer [query-and-get-product-details
                                                          product-review-page
                                                          product-reviews]]
            [data-enrichment.crawlers.snapdeal.impl :refer [product-details category-urls]]
            [data-enrichment.core :refer [enrich-with-reviews]]
            [data-enrichment.string.utils :refer [levenshtein-distance]]))

(deftest amazon-product-query-details
  (testing "Testing if the 'query and fetch details' function"
    (is (= (query-and-get-product-details "harry potter and the prisoner of azkaban")
           {:product-title "Harry Potter and the Prisoner of Azkaban (Harry Potter 3)"
            :product-url "https://www.amazon.in/Harry-Potter-Prisoner-Azkaban/dp/1408855674"}))))

(deftest amazon-product-reviews
  (testing "Testing if the 'review fetcher' function for a specific product"
    (is
      (not-empty
        (product-reviews
          (product-review-page
            "positive"
            "https://www.amazon.in/Harry-Potter-Prisoner-Azkaban/dp/1408855674"))))))

(deftest snapdeal-search-landing-page-crawl
  (testing "Testing the crawl function of snapdeal's search product landing page"
    (is (not-empty (product-details (category-urls "1"))))))

(deftest enrichment
  (testing "Testing end-to-end functionality"
    (is (not-empty (enrich-with-reviews "1")))))

(deftest string-match
  (testing "Testing levenshtein distance function"
    (is (= (levenshtein-distance "Harry Potter" "Harry Potter") 100.0))))

(deftest string-mismatch
  (testing "Testing levenshtein distance function"
           (is (< (levenshtein-distance "Harry Potter" "Harry") 60.0))))
