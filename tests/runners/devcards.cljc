(ns runners.devcards
  (:require [cljs.analyzer.api :as ana-api]
            [devcards.core :as dc :refer-macros [deftest]]))

(defn list-of-tests [test-namespace]
  (->> (ana-api/ns-publics (symbol test-namespace))
       (remove (comp :test second))
       (remove (comp :anonymous second))
       (mapcat (fn [[short-name details]]
                 (cons
                   (str "**" short-name "**")
                   (list (list (:name details))))))))

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
     ~@(tests-matching-regex test-regex)))
