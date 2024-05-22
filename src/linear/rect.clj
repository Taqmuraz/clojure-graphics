(ns linear.rect
  (:require 
    [linear.vector-xy :as xy]
  )
)

(def X 0)
(def Y 1)
(def W 2)
(def H 3)

(defn rect-min [r]
  [(r X) (r Y)]
)

(defn rect-max [r]
  [(+ (r X) (r W)) (+ (r Y) (r H))]
)

(defn contains [r p]
  (and
    (>= (p X) (r X))
    (>= (p Y) (r Y))
    (< (p X) (+ (r X) (r W)))
    (< (p Y) (+ (r Y) (r H)))
  )
)

(defn minmax [a b]
  [a (xy/sub b a)]
)

(defn volume [r] (* (r W) (r H)))

(defn intersect? [a b]
  (
    (comp zero? volume (partial apply minmax) map)
    (partial xy/clamp (rect-min a) (rect-max a))
    [(rect-min b) (rect-max b)]
  )
)