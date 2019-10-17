#!/usr/bin/env cljmd
(deps '[[me.raynes/conch "0.8.0"]
        [quarrelsome "0.1.0-SNAPSHOT"]])
(ns quarrelsome
  "Quarrelsome build tool"
  (:require
    [me.raynes.conch :as sh]
    [quarrel.core :refer [run]]))

(defn install "local install" [_]
  (prn "Installing")
  (sh/with-programs [lein]
    ;TODO for some reason `print` does not work but `println` does
    ;TODO don't capture *out* and *err* so that output can be seen
    (println (lein "install"))))

(run @#'user/*script*)
