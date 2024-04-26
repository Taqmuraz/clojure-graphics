(ns window.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [linear.vector-xy :as xy]))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  {:pos [0 0]
   :dest [0 0]})

(defn update-state [state]
  (def d (xy/sub (:pos state) (:dest state)))
  (def l (xy/len d))
  (def s 10)
  (def v
    (if
      (> l s)
      (xy/divf d l)
      [0 0]))
  (assoc {}
    :pos
    (xy/add (:pos state) (xy/mulf v s))
    :dest
    (if (q/mouse-pressed?)
      [(q/mouse-x) (q/mouse-y)]
      (:dest state)
    )
  )
)

(defn draw-state [state]
  (q/background 240)
  (q/fill 150)
  (apply q/ellipse (concat (:pos state) [100 100]))
)

(q/defsketch window
  :title "Лисьп"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode]
  :features [:keep-on-top]
)