(ns quarrel.core-test
  (:require
    [quarrel.testing]
    [io.jesi.backpack.test.macros :refer [is=]]
    [clojure.test :refer :all]
    [quarrel.core :refer :all]))

(def ^:private ns->setup #'quarrel.core/ns->setup)

(deftest ns->setup-test

  (testing "ns->setup"

    (testing "is a function"
      (is (fn? @ns->setup)))

    (testing "returns CLI-matic setup"

      (testing "for all public functions in the specified ns"

        (testing "symbol"
          (is= {:app         {:command     "testing"
                              :description ["I'm here for an argument"]
                              :version     "1"}
                :global-opts []
                :commands    [{:command     "pr"
                               :description ["Create or open an existing PR"]
                               :opts        [{:option  "open"
                                              :short   "o"
                                              :as      "Open the PR URL"
                                              ;TODO cli-matic does not support boolean types
                                              ;:type    :bool
                                              :type    :string
                                              :default false}]
                               :runs        (var quarrel.testing/pr)}]}
               (ns->setup "testing" 'quarrel.testing)))

        (testing "var")

        (testing "except those that start with -")))

    (comment (testing "performance"
               (dotimes [_ 10]
                 (time (ns->setup "perf" 'quarrel.testing)))))))

