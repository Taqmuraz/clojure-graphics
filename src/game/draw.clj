(ns game.draw
  (:require
    [quil.core :as q]
    [linear.vector-xy :as xy]
  )
)

(defn sprite
  ([img] (sprite img [0 0 1 1]))
  ([img uv]
    (let
      [
        img-w (.width img)
        img-h (.height img)
        ux (->> 0 uv (* img-w))
        uy (->> 1 uv (* img-h))
        uw (->> 2 uv (* img-w))
        uh (->> 3 uv (* img-h))
      ]
      (q/push-matrix)
      (q/translate 0 1)
      (q/scale 1 -1)
      (q/begin-shape)
      (q/texture img)
      (q/vertex 0 0 ux uy)
      (q/vertex 0 1 ux (+ uy uh))
      (q/vertex 1 1 (+ ux uw) (+ uy uh))
      (q/vertex 1 0 (+ ux uw) uy)
      (q/end-shape :close)
      (q/pop-matrix)
    )
  )
)