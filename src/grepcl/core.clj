;; TODO: tools.cli/cli is might soon be deprecated; replace with tools.cli/parse-opts when possible
;; TODO: fix cli HELP

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
    ;; zero-plus and one-plus for sets temporarily suspended as features until composition of automata is found to be possible
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

(defn pprint-result
  [re vectorized-text]
  (let [result (run-re re vectorized-text)]
    (when (:accepted? result)
      (println "The specified regular expression was found")
      (println "Please note that a display of the patten-matching component of the specified text source has not yet been implemented."))))

(defn -main
  [& args]
  (let [[opts args banner] (cli args
                                ["-a" "--help" "Help"
                                      :default false :flag true]
                                ["-r" "--regex" "Regular Expression"
                                 :validate [#(and (string? %) (> (count %) 0))
                                            "Regex must be valid string"]]
                                ["-p" "--path" "Text path"]
                                ["-v" "--visual" "Visual representation of regex as FSM"
                                 :default false :flag true])
        re (opts :regex)
        text-path (opts :path)]
    (with-open [rdr (java-io/reader text-path)]
      (let [text-as-vector (filter #(not (= % \newline)) (kern/char-seq rdr))]
        (do
          (if (:help opts) (println banner))
          (pprint-result re text-as-vector)
          (if (:visual opts) (visualize-re re)))))))
