;; TODO: write more extensive tests

(ns grepcl.core-test
  (:require [clojure.test :refer :all]
            [grepcl.core :refer :all]))

(deftest parse-re-test
  (testing "testing parse-re (example based tests)"
    (are [expected re]
         (= expected (parse-re re))
         [] ""
         [\h \e \l \l \o \space \1 \2 \3] "hello 123"
         [[\1]] "[1]"
         nil "[i*nVa1*d?]"
         [[\a] \1 \+ \b \*] "[a]1+b*")))

(deftest k->a-test
  (testing "testing k->a (example based)"
    (are [expected parsed-re]
         (= expected (k->a parsed-re))
         [] []
         '(\a \b \c) [\a \b \c])))



