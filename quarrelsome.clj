#!/usr/bin/env cljog
;TODO and slf4j-nop to cljog
(deps '[[com.clojure-goes-fast/lazy-require "0.1.1"]
        [com.github.andrethehunter/quarrelsome "0.1.0-SNAPSHOT"]])

(ns quarrelsome
  "Quarrelsome build tool"
  (:refer-clojure :exclude [test])
  (:require
    [quarrel.core :refer [run]]
    [lazy-require.core :refer [with-lazy-require]]))

(def ^:private echo println)

(defn- err [& more]
  (binding [*out* *err*]
    (apply println more)))

(defn- -lein
  ([args] (-lein [:default] args))
  ([profiles args]
   (user/deps '[[leiningen "2.9.1"]])
   (with-lazy-require [[clojure.stacktrace :as stacktrace]
                       [clojure.string :as str]
                       [leiningen.core.main :as lein]
                       [leiningen.core.project :as project]]
     (echo "Running:" "lein" (str/join \space args))
     (try
       (project/ensure-dynamic-classloader)
       (lein/resolve-and-apply (project/read "project.clj" profiles) args)
       (catch Exception e
         (stacktrace/print-cause-trace e))))))

(defn- lein [& args]
  (-lein args))

(defn- lein-dev [& args]
  (-lein [:dev] args))

(defn deps [{^{:short \t
               :tag   Boolean} tree? :tree}]
  ;TODO support passing in profile arg e.g. p deps -t test
  (let [args ["deps"]]
    (apply lein "-U" (if tree?
                       (concat ["with-profile" "-dev"] args [":tree"])
                       args))))

(defn lint [_]
  (lein-dev "docs"))

(defn lint [_]
  (lein-dev "lint"))

(defn outdated [_]
  (lein-dev "ancient"))

(def old outdated)

(defn test "run unit tests"
  [{^{:short \r
      :tag   Boolean} refresh? :refresh}]
  (lein-dev (if refresh?
              "test-refresh"
              "test") "unit"))

(defn perf "run performance tests" [_]
  (lein-dev "test" "perf"))

(defn- require-committed [& paths]
  (user/deps '[[clj-jgit "1.0.0-beta3"]])
  (with-lazy-require [[clj-jgit.porcelain :as git]]
    (when (->> (git/git-status (git/load-repo ".") :paths paths)
            vals
            (some seq))
      (err "Uncommitted files in" paths)
      (System/exit 1))))

(defn docs "generate API docs" [_]
  (lein-dev "docs"))

(defn test-docs [_]
  (docs nil)
  (require-committed "docs"))

(defn install "local install" [_]
  (echo "Installing")
  (lein "install"))

(defn snapshot [_]
  (with-lazy-require [[leiningen.core.main :as lein]]
    (lein/exit 1)))

(defn release [_]
  (with-lazy-require [[leiningen.core.main :as lein]]
    (lein/exit 1)))

(run @#'user/*script*)
