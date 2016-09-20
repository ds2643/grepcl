(ns grepcl.core-test
  (:require [clojure.test :refer :all]
            [grepcl.core :refer :all]))

(comment
(deftest parse-re-test
  (testing ""
    (are [expected re]
         (= expected (parse-re re))
         _ _
         _ _)))

(deftest k->a
  (testing ""
    (are [expected parsed-re]
         (= expected (parsed-re-to-graph parsed-re))
         _ _
         _ _)))

;; TODO: add cases
;; TODO: add exception tests
