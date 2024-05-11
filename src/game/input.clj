(ns game.input
  (:require
    [quil.core :as q]
  )
)

(defn wasd []
  (cond
    (not (q/key-pressed?)) [0 0]
    (= (q/key-as-keyword) :a) [-1 0]
    (= (q/key-as-keyword) :d) [1 0]
    (= (q/key-as-keyword) :s) [0 -1]
    (= (q/key-as-keyword) :w) [0 1]
    :else [0 0]
  )
)

(defn key-pressed? [k]
  (and (q/key-pressed?) (= (q/key-as-keyword) k))
)