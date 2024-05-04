(ns linear.matrix-2x3)

(defn viewport[w h]
  [
    (/ w 2) 0 (/ w 2)
    0 (/ h -2) (/ h 2)
  ]
)

(defn world[cam-x cam-y scale]
  [
    (/ 1 scale) 0 cam-x
    0 (/ 1 scale) cam-y
  ]
)

(defn translation[x y]
  [
    1 0 x
    0 1 y
  ]
)

(defn translation-scale [x y w h]
  [
    w 0 x
    0 h y
  ]
)