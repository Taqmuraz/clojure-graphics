(ns window.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [linear.vector-xy :as xy]))

(def fps 60)
(def delta-time (/ 1.0 60.0))

(defn limb
  ([piv len wid rot fun] (limb piv len wid rot fun []))
  ([piv len wid rot fun cld]
    (q/push-matrix)
    (apply q/translate piv)
    (q/rotate (q/radians rot))
    (q/push-matrix)
    (q/scale len wid)
    (fun)
    (q/pop-matrix)
    (dorun (map #(apply limb %) cld))
    (q/pop-matrix)
  )
)

(defn setup []
  (q/frame-rate fps)
  (q/color-mode :rgb)
  {
    :player (q/load-image "res/knight.png")
    :pos [100 100]
    :dest [100 100]})

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
  (defn bx [] (q/no-stroke) (q/fill 150) (q/rect 0 0 1 1))
  (limb [100 100] 100 50 0 bx
    [
      [[100 0] 100 40 45 bx]
    ]
  )
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