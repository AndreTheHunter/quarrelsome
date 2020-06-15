(ns quarrel.core-test
  (:require
    [clojure.test :refer :all]
    [quarrel.core :refer :all]
    [clojure.tools.cli :refer [parse-opts]]
    [quarrel.testing]))

(def testing-ns (find-ns 'quarrel.testing))

(deftest cli-options-test

  (testing "option-specs"

    (testing "is a function"
      (is (fn? option-specs)))

    (testing "returns tools.cli options map for all public functions in the specified ns"
      (is (some? testing-ns))
      ;TODO use order agnostic equals
      (let [option-specs (option-specs "testing" testing-ns)]
        ;FIXME make tools.cli compatible map
        (is (= {:commands [{:command     "pr"
                            :runs        #'quarrel.testing/pr
                            :opts        [{:id        "open"
                                           :as        "Open the PR URL"
                                           :short-opt "o"
                                           :type      :with-flag}]
                            :description ["Create or open an existing PR"]}]
                :app      {:command     "testing"
                           :description ["I'm here for an argument"]}}
               option-specs))
        (is (nil? (:errors (parse-opts ["-h"] option-specs))))))))

(comment (deftest ^:performance cli-options-performance-test

           (testing "cli-options performance"
             (is (some? testing-ns))
             (dotimes [_ 10]
               (time (option-specs "perf" testing-ns))))))

(deftest assoc-some-test

  (testing "assoc-some"
    (let [m {:a 1}]

      (testing "assocs when value is some"
        (is (identical? m (assoc-some m :b nil)))
        (is (= {:a 1 :b 2}
               (assoc-some m :b 2))))

      (testing "supports multiple keys"
        (is (= {:a 1
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
                 :g {})))))))
