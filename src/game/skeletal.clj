(ns game.skeletal
  (:require
    [quil.core :as q]
    [game.draw :as draw]
    [linear.matrix-2x3 :as mat2]
  )
)

(defn limb [img uv piv len wid rot & chld]
  (draw/sprite img (mat2/translation-rotation-scale 0 0 rot len wid) piv uv)
)