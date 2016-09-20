(ns runners.whatever-test
  (:require [cljs.env]
            [cljs.analyzer :as ana]
            [cljs.analyzer.api :as ana-api]))

(defmacro my-bs-macro []
  `(prn (ana-api/all-ns)))

