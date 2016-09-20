(defproject grepcl "0.1.0-SNAPSHOT"
  :description "Global regular expression parser (grep) implemented in Clojure"
  :dependencies [[automat "0.2.0"] ;; updating to add
                 [org.clojure/clojure "1.8.0"] 
                 [aysylu/loom "0.6.0"] ;; currently in use
                 [org.blancas/kern "1.0.0"] ;; in use
                 [instaparse "1.4.3"] ;; depr
                 [cljcc "0.1.3"] ;; depr
                 [clj-antlr "0.2.3"]] ;; depr
  :main ^:skip-aot grepcl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
