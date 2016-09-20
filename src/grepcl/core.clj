[](ns grepcl.core
  (:gen-class)
  (:require [blancas.kern.core :as kern] 
            [blancas.kern.lexer.java-style :as kern-lexer]
            [loom.graph :as loom-graph]
            [loom.io :as loom-io] 
            [clojure.java.io :as java-io] 
            [automat.viz :as automat-viz] 
            [automat.core :as automat]))

;; grammar
;; token  ::= any-char | n-times | zero-plus | one-plus | one-of | char | digit
;; oneOf  ::= '[' (char (char)*)* ']'
;; char   ::= number | digit 

(defn parse-re
    "apply kern parsing combinators to string re representation"
    [re]
    (let [one-of
           (kern-lexer/brackets
             (kern/many
               (kern/<|>
                 kern/letter kern/digit)))
          zero-plus
            (kern/sym* \*) ;; TODO should only be valid after bracket
          one-plus
            (kern/sym* \+) ;; TODO should only be valid after bracket
          n-times
            (kern-lexer/braces 
              (kern/<*>
                kern/digit
                kern-lexer/comma))
          any-char
            (kern/sym* \.)
          space
            (kern/sym* \space)] ;; TODO fix space
      (kern/value
        (kern/many
          (kern/<|> space     ;; TODO fix
                    any-char 
                    n-times   ;; TODO poor context handling
                    zero-plus ;; TODO poor context handling
                    one-plus  ;; TODO poor context handling
                    one-of 
                    ;; start
                    ;; not
                    ;; maybe char-?
                    kern/letter 
                    kern/digit)) re)))

(defn k->a
  "converts kern parsing output to valid automat fsm input"
  [k]
  (cond
    (nil? (first k)) []
    ;;invalid token [temporary until error handling is added in parse-re] 
    ;; n-times
    ;; zero-plus
    (and (vector? (first k))                ;; TODO treated as group
         (= (second k) \*)) (cons
                              (eval (cons automat/*
                                          (flatten (first k))))
                              (k->a (rest (rest k))))
    ;; one-plus
    ;;(and (vector? (first k))
    ;;     (= (second k) \+)) (cons
    ;;                          (eval (cons automat/+ 
    ;;                                (flatten (first k))))
    ;;                          (k->a (rest (rest k))))
    (vector? (first k)) (cons 
                          (eval (cons automat/or
                                (flatten (first k))))
                          (k->a (rest k)))
    ;; any-char
    (= (first k) \.) (cons
                        (first k)
                        (k->a (rest k)))
    :else (cons 
            (first k)
            (k->a (rest k)))))


(defn -main
  [& args]
  ;;(def abs-file-path "/home/david/Projects/temp/poop.txt")
  ;;(with-open [rdr (java-io/reader abs-file-path)]
  ;;  (let [c (kern/char-seq rdr)]
  ;;    (println (take 5 c))))
  ;;(automat-viz/view (k->a (parse-re "a[bc]d"))))
  (k->a (parse-re "[ef]+")))

;;(defn parsed-re->graph-params
;;  "generates directed weighted graph from parsed re"
;;  [parsed-re]
;;  (eval (cons 'loom.graph/weighted-digraph (map (fn [x] (into [] (concat (map (fn [x] (keyword (str x))) (first x)) (second x)))) (zipmap (interleave (map vector (range) (iterate inc 1))) (map (fn [x] (vector x)) parsed-re ))))))
