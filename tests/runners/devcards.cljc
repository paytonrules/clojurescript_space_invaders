(ns runners.devcards
  (:require [cljs.analyzer.api :as ana-api]
            [devcards.core :as dc :refer-macros [deftest]]))

(defn list-of-tests [test-namespace]
  (->> (ana-api/ns-publics (symbol test-namespace))
       (remove (comp :test val))
       (remove (comp :anonymous val))
       (mapcat (fn [[short-name details]]
                 (cons
                   (str "**" short-name "**")
                   (list (list (:name details))))))))

(defn tests-matching-regex [test-regex]
  (->> (ana-api/all-ns)
       (map str)
       (filter #(re-matches test-regex %))
       (sort)
       (mapcat (fn [test-ns]
                 (cons
                   (str "### " test-ns)
                   (list-of-tests test-ns))))))

(defmacro dev-cards-runner [test-regex]
  `(dc/deftest ~'all-test
     ~@(tests-matching-regex test-regex)))
