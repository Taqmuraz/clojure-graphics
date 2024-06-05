(ns linear.matrix-2x3
  (:require
    [linear.vector-xy :as xy]
    [clojure.math :as math]
  )
)

(defn viewport[w h]
  [
    (/ w 2) 0 (/ w 2)
    0 (/ h -2) (/ h 2)
  ]
)

(defn ortho[cam-x cam-y scale aspect]
  [
    (/ 1 scale aspect) 0 (/ cam-x scale aspect)
    0 (/ 1 scale) (/ cam-y scale)
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

(def HPI (/ math/PI 2))

(defn translation-rotation [x y r]
  [
    (math/cos r) (->> r (+ HPI) math/cos) x
    (math/sin r) (->> r (+ HPI) math/sin) y
  ]
)
(defn translation-rotation-scale [x y r sx sy]
  [
    (* sx (math/cos r)) (->> r (+ HPI) math/cos (* sy)) x
    (* sx (math/sin r)) (->> r (+ HPI) math/sin (* sy)) y
  ]
)

(defn translation-dir [x y dx dy]
  [
    dx (- dy) x
    dy dx     y
  ]
)