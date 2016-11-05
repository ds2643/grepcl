(ns grepcl.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli]]
            [blancas.kern.core :as kern]
            [blancas.kern.lexer.java-style :as kern-lexer]
            [clojure.java.io :as java-io]
            [automat.viz :as automat-viz]
            [automat.core :as automat]))

(defn parse-re
    "validate regex string; output stream of chars"
    [re]
    	(kern/value
          	(kern/many
          	  (kern/<|> (kern/sym* \space) ; space
          	            (kern/sym* \.) ; wildcard
                            (kern/sym* \+) ; one-plus (singleton)
                            (kern/sym* \*) ; zed-plus (singleton)
          	            ;; zero-plus multichar ;; requires composable automata
          	            ;; one-plus  multichar ;; requires composable automata
          	            (kern-lexer/brackets
          	              (kern/many kern/alpha-num)) ; selection from numbers and/or letters
          	            kern/alpha-num)) re))

(defn k->a
  "converts kern parsing output to valid automat fsm input"
  [k]
  (cond
    (nil? (first k)) []
    ;; zero-plus and one-plus for sets temporarily suspended as features until composition
    ;; of automata is found to be possible
    ;; zero-plus (for sets)
    ;;(and (vector? (first k))
    ;;     (= (second k) \*)) (cons
    ;;                          (eval (cons automat/*
    ;;                                      (flatten (first k))))
    ;;                          (k->a (rest (rest k))))
    ;; one plus (for sets)
    ;;(and (vector? (first k))
    ;;     (= (second k) \+)) (cons (cons automat/+
    ;;                                (eval (cons automat/or (flatten (first k)))))
    ;;                        (k->a (rest (rest k))))
    ;; TODO: implement 'range', 'maybe', and 'not'
    (and (not (vector? (first k))) ;; zed-plus singleton
         (= (second k) \*)) (cons (eval (automat/* (first k)))
                              (k->a (rest (rest k))))
    (and (not (vector? (first k))) ;; one-plus singleton
         (= (second k) \+)) (cons (eval (automat/+ (first k)))
                              (k->a (rest (rest k))))
    (vector? (first k)) (cons  ; from set
                          (eval (cons automat/or
                                (flatten (first k))))
                          (k->a (rest k)))
    (= (first k) \.) (cons  ; wildcard
                        (eval automat/any)
                        (k->a (rest k)))
    :else (cons
            (first k)
            (k->a (rest k)))))

(defn build-automat-fsm
  "build automat fsm"
  [re]
  (-> re
      parse-re
      k->a))

(defn run-re
  "returns result of running specified text against a regular expression"
  [re text]
  (let [re-fsm (automat/compile (build-automat-fsm re))]
    (automat/find re-fsm nil text)))

(defn visualize-re
  "graphviz rendering of regular expression as a finite state machine"
  [re]
  (automat.viz/view (build-automat-fsm re)))

(defn -main
  [args]
  (let [re "hello"
        text-path "test.txt"
        [opts args banner] (cli args ["-h" "--help" "Help"
                                      :default false :flag true])
        re "hello"]
    (with-open [rdr (java-io/reader text-path)]
      (let [text-as-vector (filter #(not (= % \newline)) (kern/char-seq rdr))]
        (do
          (if (:help opts) (println banner))
          (println (run-re re text-as-vector)))))))
