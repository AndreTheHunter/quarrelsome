(ns quarrel.core-test
  (:refer-clojure :exclude [=])
  (:require
    [io.jesi.customs.strict :refer :all]
    [quarrel.core :refer :all]
    [quarrel.testing]))

(deftest ns->setup-test

  (testing "ns->setup"

    (testing "is a function"
      (is (fn? ns->setup)))

    (testing "returns CLI-matic setup"

      (testing "for all public functions in the specified ns"
        (is= {:app      {:command     "testing"
                         :description ["I'm here for an argument"]}
              :commands [{:command     "pr"
                          :description ["Create or open an existing PR"]
                          :opts        [{:option  "open"
                                         :short   "o"
                                         :as      "Open the PR URL"
                                         :type    :with-flag
                                         :default false}]
                          :runs        (var quarrel.testing/pr)}]}
             (ns->setup "testing" (find-ns `quarrel.testing)))))))

(deftest ^:performance ns->setup-performance-test

  (testing "ns->setup performance"
    (dotimes [_ 10]
      (time (ns->setup "perf" 'quarrel.testing)))))

(def ^:private assoc-some #'quarrel.core/assoc-some)

(deftest assoc-some-test

  (testing "assoc-some"
    (let [m {:a 1}]

      (testing "assocs when value is some"
        (is (identical? m (assoc-some m :b nil)))
        (is= {:a 1 :b 2}
             (assoc-some m :b 2)))

      (testing "supports multiple keys"
        (is= {:a 1
              :b "b"
              :c 3
              :e true
              :f false
              :g {}}
             (assoc-some m
               :b "b"
               :c 3
               :d nil
               :e true
               :f false
               :g {}))))))
