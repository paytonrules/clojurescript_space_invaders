(ns runners.devcards
  (:require [cljs.analyzer.api :as ana-api]
            [devcards.core :as dc :refer-macros [deftest]]))

(defn list-of-tests [test-namespace]
  (->> (ana-api/ns-publics (symbol test-namespace))
       (map second)
       (filter :test)
       (map (comp list :name))))

(defn tests-matching-regex [test-regex]
  (->> (map str (ana-api/all-ns))
       (filter #(re-matches test-regex %))
       (sort)
       (mapcat (fn [test-ns]
                 (cons
                   (str "### " test-ns)
                   (list-of-tests test-ns))))))

(defmacro dev-cards-runner [test-regex]
  `(dc/deftest ~'all-test
     ~"# All Tests"
     ~@(tests-matching-regex test-regex)))
