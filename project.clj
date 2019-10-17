(defproject quarrelsome "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :managed-dependencies [[org.clojure/clojure "1.10.1"]]
  :dependencies [[org.clojure/clojure]
                 [cli-matic "0.3.8"]
                 [com.taoensso/encore "2.116.0"]
                 [io.jesi/backpack "3.4.1"
                  :exclusions [com.lucasbradstreet/cljs-uuid-utils
                               org.clojars.mmb90/cljs-cache
                               org.clojure/core.async]]]
  :repl-options {:init-ns quarrel.core}
  :profiles {:dev {:plugins [[lein-auto "0.1.3"]
                             [lein-shell "0.5.0"]
                             [venantius/ultra "0.6.0"]]}}
  :aliases {"test-refresh" ["auto" ["do" ["shell" "clear"] "test"]]})

