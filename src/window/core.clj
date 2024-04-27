(ns window.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [linear.vector-xy :as xy]))

(def fps 60)
(def delta-time (/ 1.0 60.0))

(defn setup []
  (q/frame-rate fps)
  (q/color-mode :hsb)
  {
    :player (q/load-image "res/knight.png")
    :pos [0 0]
    :dest [0 0]})

(defn update-state [state]
  (def d (xy/sub (state :dest) (state :pos)))
  (def l (xy/len d))
  (def s (* delta-time 200))
  (def v
    (if
      (> l s)
      (xy/divf d l)
      [0 0]))
  (assoc {}
    :pos
    (xy/add (state :pos) (xy/mulf v s))
    :dest
    (if (q/mouse-pressed?)
      [(q/mouse-x) (q/mouse-y)]
      (state :dest)
    )
    :player (state :player)
  )
)

(defn draw-state [state]
  (q/background 240)
  (q/fill 150)
  (def size [100 100])
  (apply q/image (concat [(state :player)] (xy/sub (state :pos) (xy/mulf size 0.5)) size))
)
(defn -main [& args]
  (q/defsketch window
    :title "Лисьп"
    :size [500 500]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]
  )
)