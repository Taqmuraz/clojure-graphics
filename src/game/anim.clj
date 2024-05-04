(ns game.anim
  (:require
    [quil.core :as q]
    [linear.vector-xy :as xy]
  )
)

(defn anim [fs n]
  (fn [t]
    (fs (int (mod (* t n) n)))
  )
)

(defn load-anim [img ew eh]
  (while (not (q/loaded? img)) ())
  (def size [(.width img) (.height img)])
  (def num (/ (size 0) ew))
  (def res
    (vec
      (map
        #(do [% (q/create-image ew eh :argb)])
        (range num)
      )
    )
  )
  (doseq [[i frame] res]
    (q/copy
      img
      frame
      [(* ew i) 0 ew eh]
      [0 0 ew eh]))
  (anim (vec (map xy/get-y res)) num)
)

