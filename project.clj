(defproject grepcl "0.1.0-SNAPSHOT"
  :description "Global regular expression parser (grep) implemented in Clojure"
  :dependencies [[automat "0.2.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.blancas/kern "1.0.0"]
                 [org.clojure/tools.cli "0.2.4"]]
  :main ^:skip-aot grepcl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
