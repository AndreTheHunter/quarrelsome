(ns quarrel.testing
  "I'm here for an argument"
  (:refer-clojure :exclude [pr]))

(def v nil)

(def ^:private x nil)

(defn- private [])

(defn -main [& args])

;FIXME rename to something more to the theme
(defn pr
  "Create or open an existing PR"
  [{^{:short \o
      :doc   "Open the PR URL"
      :tag   Boolean} open? :open
    :or                  {open? false}}])
