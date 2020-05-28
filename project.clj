;TODO com.github.andrethehunter ?
(defproject com.github.andrethehunter/quarrelsome "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/AndreTheHunter/quarrelsome"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :managed-dependencies [[cheshire "5.10.0"]
                         [com.cognitect/transit-clj "1.0.324"]
                         [com.google.code.findbugs/jsr305 "3.0.2"]
                         [com.google.errorprone/error_prone_annotations "2.3.4"]
                         [expound "0.8.4"]
                         [io.jesi/backpack "5.2.2"]
                         [org.clojure/spec.alpha "0.2.187"]
                         [org.clojure/tools.cli "1.0.194"]
                         [org.clojure/tools.namespace "1.0.0"]]
  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [cli-matic "0.3.11"]
                 [com.taoensso/encore "2.120.0"]]
  :repl-options {:init-ns quarrel.core}
  :profiles {:dev {:plugins      [[lein-ancient "0.6.15"]
                                  [lein-codox "0.10.7"]
                                  [lein-nsorg "0.3.0"]]
                   :dependencies [[clj-kondo "RELEASE"]
                                  [io.jesi/backpack]
                                  [io.jesi/customs "1.1.2"]
                                  [lambdaisland/kaocha "1.0.632"]]
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
