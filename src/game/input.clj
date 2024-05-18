(ns game.input
  (:require
    [quil.core :as q]
  )
)

(defn wasd []
  (cond
    (not (q/key-pressed?)) [0 0]
    :else (case (q/key-as-keyword)
      :a [-1 0]
      :d [1 0]
      :s [0 -1]
      :w [0 1]
      [0 0]
    )
  )
)

(defn key-pressed? [k]
  (and (q/key-pressed?) (= (q/key-as-keyword) k))
)