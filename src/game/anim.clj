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

(defn anim-offset [a o]
  (comp a - (partial - o))
)

(defn load-anim [img ew eh]
  (while (not (q/loaded? img)) ())
  (def size [(.width img) (.height img)])
  (def img-num (/ (size 0) ew))
  (def res
    (map
      #(do
        (let [frame (q/create-image ew eh :argb)]
          (q/copy
            img
            frame
            [(* ew %) 0 ew eh]
            [0 0 ew eh]
          )
          frame
        )
      )
      (range img-num)
    )
  )
  (anim (vec res) img-num)
)

