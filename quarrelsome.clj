#!/usr/bin/env cljog
(deps '[[me.raynes/conch "0.8.0"]
        [quarrelsome "0.1.0-SNAPSHOT"]])
(ns quarrelsome
  "Quarrelsome build tool"
  (:refer-clojure :exclude [test])
  (:require
    [me.raynes.conch :as sh]
    [quarrel.core :refer [run]]))

;TODO faster to run leiningen as a library?

(defn test "run unit tests" [{^{:short \r
                                :tag   Boolean} refresh? :refresh}]
  (sh/with-programs [lein]
    (println (lein (if refresh? "test-refresh" "test")))))

(defn install "local install" [_]
  (println "Installing")
  (sh/with-programs [lein]
    ;TODO for some reason `print` does not work but `println` does
    ;TODO don't capture *out* and *err* so that output can be seen
    (println (lein "install"))))

(run @#'user/*script*)
