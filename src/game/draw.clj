(ns game.draw
  (:require
    [quil.core :as q]
  )
)

(defn sprite [img mat]
  (q/push-matrix)
  (apply q/apply-matrix mat)
  (q/translate -0.5 0.5)
  (q/scale 1 -1)
  (q/image img 0 0 1 1)
  (q/pop-matrix)
)