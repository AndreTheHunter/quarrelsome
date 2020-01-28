(defproject quarrelsome "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/AndreTheHunter/quarrelsome"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :managed-dependencies [[cheshire "5.9.0"]
                         [com.cognitect/transit-clj "0.8.319"]
                         [com.google.code.findbugs/jsr305 "3.0.2"]
                         [com.google.errorprone/error_prone_annotations "2.1.3"]
                         [io.jesi/backpack "5.0.0-SNAPSHOT"]
                         [org.clojure/tools.namespace "0.3.1"]]
  :exclusions [pjstadig/humane-test-output org.clojars.mmb90/cljs-cache org.clojure/clojurescript com.cognitect/transit-js]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cli-matic "0.3.11"]
                 [com.taoensso/encore "2.119.0"]]
  :repl-options {:init-ns quarrel.core}
  :profiles {:dev {:plugins      [[lein-nsorg "0.3.0"]
                                  [lein-codox "0.10.7"]]
                   :dependencies [[clj-kondo "RELEASE"]
                                  [io.jesi/backpack]
                                  [io.jesi/customs "1.0.0-SNAPSHOT"]
                                  [lambdaisland/kaocha "0.0-565"]]
                   :codox        {:output-path "docs"}}}
  :aliases {"kaocha"          ["run" "-m" "kaocha.runner"]
            "clj-kondo"       ["run" "-m" "clj-kondo.main"]
            "test"            ["kaocha"]
            "tests"           ["kaocha" "--focus"]
            "test-refresh"    ["kaocha" "--watch"]
            "lint-nsorg"      ["nsorg" "--replace"]
            "lint-kondo"      ["clj-kondo" "--" "--cache" "--lint" "src"]
            "lint-test-kondo" ["clj-kondo" "--" "--cache" "--lint" "test"]
            "lint"            ["do"
                               ["lint-nsorg"]
                               ["check"]
                               ["lint-kondo"]
                               ["lint-test-kondo"]]
            "docs"            ["codox"]})
