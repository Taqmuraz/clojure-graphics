(ns game.time
  (:require
    [quil.core :as q]
  )
)

(defn fps [] 60)

(defn delta-time [] (/ 1.0 60.0))

(defn time[]
  (* (q/millis) 0.001)
)

(defn anim [fs n]
  (fn [t]
    (fs (int (mod (* t n) n)))
  )
)