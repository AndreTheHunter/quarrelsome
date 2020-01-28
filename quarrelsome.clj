#!/usr/bin/env cljog
(deps '[[com.clojure-goes-fast/lazy-require "0.1.1"]
        [quarrelsome "0.1.0-SNAPSHOT"]
        ;TODO load lazily once cljog supports lazy deps
        [leiningen "2.9.1"]
        [clj-jgit "1.0.0-beta3"]])
(ns quarrelsome
  "Quarrelsome build tool"
  (:refer-clojure :exclude [test])
  (:require
    [quarrel.core :refer [run]]
    [lazy-require.core :refer [with-lazy-require]]))

(def echo println)

(defn- err [& more]
  (binding [*out* *err*]
    (apply println more)))

(defn- project
  ([] (project [:default]))
  ([profiles]
   (with-lazy-require [[leiningen.core.project :as project]]
     (project/ensure-dynamic-classloader)
     (project/read "project.clj" profiles))))

(defn- -lein [project args]
  (with-lazy-require [[clojure.stacktrace :as stacktrace]
                      [clojure.string :as str]
                      [leiningen.core.main :as lein]]
    (try
      (echo "Running:" "lein" (str/join \space args))
      (lein/resolve-and-apply project args)
      (catch Exception e
        (stacktrace/print-cause-trace e)))))

(defn- lein [& args]
  (-lein (project) args))

(defn- lein-dev [& args]
  (-lein (project [:dev]) args))

(defn deps [_]
  (lein "-U" "deps"))

(defn lint [_]
  (lein-dev "docs"))

(defn lint [_]
  (lein-dev "lint"))

(defn test "run unit tests"
  [{^{:short \r
      :tag   Boolean} refresh? :refresh}]
  (lein-dev (if refresh?
              "test-refresh"
              "test")))

(defn- require-committed [& paths]
  (with-lazy-require [[clj-jgit.porcelain :as git]
                      [leiningen.core.main :as lein]]
    (when (->> (git/git-status (git/load-repo ".") :paths paths)
            vals
            (some seq))
      (err "Uncommitted files in" paths)
      (lein/exit 1))))

(defn docs "generate API docs"[_]
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
