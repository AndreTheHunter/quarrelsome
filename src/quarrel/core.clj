(ns quarrel.core
  (:require
    [cli-matic.core :as cli]
    [clojure.string :as str]
    [taoensso.encore :refer [assoc-some]])
  (:import
    (clojure.lang Keyword)))

(defn- tag->type [tag]
  ;https://github.com/l3nz/cli-matic/blob/master/README.md#current-pre-sets
  (condp #(= %1 %2) tag
    Boolean :with-flag
    Integer :int
    Float :float
    Keyword :keyword
    :string))

(defn- char-at= [^CharSequence s fn-i ^Character char]
  (and s
       (< 0 (.length s))
       (-> s
           (.charAt (fn-i))
           (= char))))

(defn- starts-with? [^CharSequence s char]
  (char-at= s (constantly 0) char))

(defn- ends-with? [^CharSequence s char]
  (char-at= s #(dec (.length s)) char))

(defn- option [k sym]
  (let [{short-opt :short
         desc      :doc
         tag       :tag
         :keys     [type]} (meta sym)
        k (name k)
        tag (if (ends-with? (str sym) \?) Boolean tag)]
    (assoc-some {:option k}
      :short (when short-opt (str short-opt))
      :type (or type (tag->type tag))
      :as desc)))

(defn- arglist->options [[opts]]
  ;TODO support var-args
  ;TODO support :keys based destructuring
  (when (seqable? opts)
    (->> opts
         (reduce
           (fn [m [k v]]
             (cond
               (symbol? k) (assoc m
                             (keyword k) (option v k))
               (= :or k) (reduce
                           (fn [m [sym default]]
                             (assoc-in m [(keyword sym) :default] default))
                           m
                           v)
               :else m))
           {})
         vals
         vec)))

(def ^:private ns-public-fns-xform
  (comp
    (remove (comp #(starts-with? % \-) str first))
    (filter (comp fn? deref second))
    (map second)))

(defn- fnvar->subcommands [v]
  (let [{:keys [name arglists doc]} (meta v)
        name (str name)]
    ;TODO cli-matic should not invoke with {:_arguments []} when no arguments
    (assoc-some {:runs    v
                 :command name}
      :opts (arglist->options (first arglists))
      :description (or (some-> doc str/split-lines) name))))

(defn ns->setup [cmd-name ns]
  (let [{ns-doc :doc} (meta ns)]
    ;TODO if `-main` use that as only command
    {:app      (assoc-some {:command cmd-name}
                 :description (some-> ns-doc str/split-lines))
     :commands (->> ns
                    ns-publics
                    (eduction ns-public-fns-xform)
                    (map fnvar->subcommands))}))

(defn main [app-name args]
  (cli/run-cmd args (ns->setup app-name *ns*)))

(defn run [app-name]
  (main app-name *command-line-args*))
