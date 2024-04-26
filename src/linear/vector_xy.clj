(ns linear.vector-xy
  (:require [clojure.math :as math]))

(defn get-x [v] (v 0))
(defn get-y [v] (v 1))

(defn apply-func [f]
  (fn [& vs]
    [
      (apply f (map get-x vs))
      (apply f (map get-y vs))
    ]
  )
)

(def add (apply-func +))
(def sub (apply-func -))
(def mul (apply-func *))
(def div (apply-func /))
(defn mulf [v f]
  [
    (* (v 0) f)
    (* (v 1) f)
  ]
)
(defn divf [v d]
  (if
    (= (double d) 0.0)
    [0 0]
    [(/ (v 0) d) (/ (v 1) d)]
  )
)

(defn dot [a b] (apply + (add a b)))
(defn len [v] (math/sqrt (dot v v)))
(defn norm [v] (divf v (len v)))