(ns game.input
  (:require
    [quil.core :as q]
    [linear.vector-xy :as xy]
  )
)

(def keys-pressed (atom (hash-set)))

(defn get-keys-pressed [] @keys-pressed)

(defn key-pressed? [k]
  (contains? (get-keys-pressed) k)
)

(defn key-pressed [s info]
  (swap! keys-pressed
    #(conj % (info :key))
  )
  s
)

(defn key-released [s info]
  (swap! keys-pressed
    #(disj % (info :key))
  )
  s
)

(defn wasd []
  (->>
    [
      [:w [0 1]]
      [:a [-1 0]]
      [:s [0 -1]]
      [:d [1 0]]
    ]
    (filter (comp key-pressed? first))
    (map second)
    (apply xy/add)
    (xy/norm)
  )
)