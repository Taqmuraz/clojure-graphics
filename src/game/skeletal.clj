(ns game.skeletal
  (:require
    [quil.core :as q]
    [linear.matrix-2x3 :as mat2]
  )
)

(defn limb [img uv [px py] [pivx pivy] [sx sy] rot & chld]
  (fn [sp]
    (q/push-matrix)
    (q/translate px py)
    (q/rotate rot)
    (q/push-matrix)
    (q/translate (->> pivx (* sx) -) (->> pivy (* sy) -))
    (q/scale sx sy)
    (sp img uv)
    (q/pop-matrix)
    (doseq [c chld] (c sp))
    (q/pop-matrix)
  )
)