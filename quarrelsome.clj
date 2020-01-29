#!/usr/bin/env cljog
(deps '[[com.clojure-goes-fast/lazy-require "0.1.1"]
        [quarrelsome "0.1.0-SNAPSHOT"]])
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
