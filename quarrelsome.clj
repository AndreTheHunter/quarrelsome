#!/usr/bin/env bb-cljog
#!/usr/bin/env cljog --bb

;
;(def echo println)
;
;(defn err [& more]
;  (binding [*out* *err*]
;    (apply println more)))
;
;(require
;  '[clojure.java.shell :refer [sh]]
;  '[babashka.classpath :refer [add-classpath]])
;
;(defn add-deps [deps]
;  (let [edn (prn-str {:deps deps})
;        {:keys [exit out err]} (sh "clojure" "-Spath" "-Sdeps" edn)]
;    (when-not (zero? exit)
;      (err "error adding deps")
;      (echo out)
;      (user/err err))
;    (echo "Updating classpath:" out)
;    (add-classpath out)))
;

;(add-deps '{com.github.andrethehunter/quarrelsome {:mvn/version "0.1.0-SNAPSHOT"}

;(add-deps '{cli-matic {:mvn/version "0.4.2"}})

(ns quarrelsome
  "Quarrelsome build tool"
  (:refer-clojure :exclude [test])
  (:require
    [babashka.classpath :refer [add-classpath]]
    [cljog :refer [echo err deps script] :rename {deps add-deps}]
    [clojure.string :as str]
    [clojure.test :as t]))

(defn sh [& cmd]
  (let [cmd-str (str/join \space cmd)]
    (echo "Running:" cmd-str)
    (let [p (-> (ProcessBuilder. cmd)
                (.redirectOutput java.lang.ProcessBuilder$Redirect/INHERIT)
                (.redirectError java.lang.ProcessBuilder$Redirect/INHERIT)
                (.start))
          exit (-> p (.waitFor))]
      (when-not (zero? exit)
        (throw (ex-info (str "Error running: " cmd-str) {:exit exit})))
      (-> p .destroy))))

(defn- ->str [coll]
  (map str coll))

(defn- -lein [args]
  (apply sh "lein" args))

(defmacro lein [& args]
  `(-lein '~(->str args)))

(defmacro lein-dev [& args]
  `(-lein '~(->str (list* 'with-profile '+dev args))))

(defn- circleci [& args]
  (apply sh "circleci" args))

(defn deps [{^{:short \t
               :tag   Boolean} tree? :tree}]
  ;TODO support passing in profile arg e.g. p deps -t test
  (let [args ["deps"]]
    (apply lein "-U" (if tree?
                       (concat ["with-profile" "-dev"] args [":tree"])
                       args))))

(defn lint [_]
  (circleci "config" "validate")
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

(defmacro git [& args]
  `(apply sh "git" '~(->str args)))

(defn- check-committed [& paths]
  (git diff --quiet --exit-code "."))

(defn docs "generate API docs" [_]
  (macroexpand-1 `(lein-dev docs))
  (lein-dev "docs"))

(defn test-docs [_]
  (docs nil)
  (check-committed "docs"))

(defn install "local install" [_]
  (echo "Installing")
  (lein "install"))

(defn snapshot [_]
  (System/exit 1))

(defn release [_]
  (System/exit 1))

;(require '[quarrel.core :refer [run]])
;(run file-name)
;(lint nil)

(defn bb-test []
  (add-classpath "src:test:resources")
  (require '[quarrel.core-test])
  (let [{:keys [fail error] :as result} (t/run-tests 'quarrel.core-test)]
    (when-not (zero? (+ fail error))
      (throw (ex-info "Tests failed" result)))))

(bb-test)
(System/exit 0)
