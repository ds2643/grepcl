;; TODO: write more extensive tests

(ns grepcl.core-test
  (:require [clojure.test :refer :all]
            [grepcl.core :refer :all]))

(deftest parse-re-test
  (testing "example based testing for pare-re attemps to parse simple examples that attempt to maximize branch coverage"
    (are [expected re]
         (= expected (parse-re re))
         [] ""
         [\space] " "
         [\l \i \s \p] "lisp"
         [\l \i \s \p \space \l \i \v \e \s] "lisp lives"
         [[]] "[]"
         [[\1 \2 \3]] "[1 2 3]"
         [[\a] \1 \+ \b \* \F \.] "[a]1+b*F."
         nil "[i*nVa1*d?]")))

(deftest k->a-test
  (testing ")testing k->a (example based)"
    (are [expected parsed-re]
         (= expected (k->a parsed-re))
         # TODO: more extensive examples
         [] []
         '(\a \b \c) [\a \b \c])))



