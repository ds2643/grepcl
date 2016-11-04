(ns grepcl.core
  (:gen-class)
  (:require [blancas.kern.core :as kern]
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
  (let [t (parse-re text)
        re-fsm (automat/compile (build-automat-fsm re))]
    (automat/find re-fsm nil t)))

(defn visualize-re
  "graphviz rendering of regular expression as a finite state machine"
  [re]
  (automat.viz/view (build-automat-fsm re)))

(defn -main
  "temporary -main bypasses cli interface"
  [& args]
  (println "0")
  )

;;(defn -main
;;  [& args]
  ;;(def abs-file-path "/home/david/Projects/temp/poop.txt")
  ;;(with-open [rdr (java-io/reader abs-file-path)]
  ;;  (let [c (kern/char-seq rdr)]
  ;;    (println (take 5 c))))
  ;;(automat-viz/view (k->a (parse-re "a[bc]d"))))
;;  (k->a (parse-re "[ef]+")))

;;(defn parsed-re->graph-params
;;  "generates directed weighted graph from parsed re"
;;  [parsed-re]
;;  (eval (cons 'loom.graph/weighted-digraph (map (fn [x] (into [] (concat (map (fn [x] (keyword (str x))) (first x)) (second x)))) (zipmap (interleave (map vector (range) (iterate inc 1))) (map (fn [x] (vector x)) parsed-re ))))))
