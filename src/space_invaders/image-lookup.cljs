(ns space-invaders.image-lookup
  (:require [clojure.string :as string]))

(defn ->image-path [character state]
  (str "images/" (name character) "_" (name state) ".png"))

(defn ->image [state image variant]
  (get-in state [:images image variant]))

(defn character-states->image-path [character-states]
  (map
    (fn [{:keys [character state]}]
      (->image-path character state))
    character-states))

(defn image-path->character-state [path]
  (map keyword (-> (string/split path "/")
                   (last)
                   (string/replace ".png" "")
                   (string/split "_"))))

(defn image-list->lookup-table [images]
  (reduce
    (fn [table image]
      (assoc-in
        table
        (image-path->character-state (.-src image))
        image))
    {}
    images))
