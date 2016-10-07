(ns space-invaders.invaders)

(defn pose [ticks]
  (if (even? ticks)
    :open
    :closed))

