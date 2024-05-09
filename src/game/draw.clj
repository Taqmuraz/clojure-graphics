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
  (q/begin-shape)
  (q/texture img)
  (q/vertex 0 0 0 0)
  (q/vertex 0 1 0 512)
  (q/vertex 1 1 512 512)
  (q/vertex 1 0 512 0)
  (q/end-shape :close)
  (q/pop-matrix)
)