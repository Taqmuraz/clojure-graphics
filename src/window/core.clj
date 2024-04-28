(ns window.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [linear.vector-xy :as xy]))

(def fps 60)
(def delta-time (/ 1.0 60.0))

(defn sprite [img mat]
  (q/push-matrix)
  (apply q/apply-matrix mat)
  (q/translate 0 1)
  (q/scale 1 -1)
  (q/image img 0 0 1 1)
  (q/pop-matrix)
)

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
    (q/translate (* wid -0.5) 0)
    (dorun (map #(apply limb %) cld))
    (q/pop-matrix)
  )
)

(defn time[]
  (* (q/millis) 0.001)
)

(defn setup []
  (q/frame-rate fps)
  (q/color-mode :rgb)
  {
    :player (q/load-image "res/knight.png")
    :pos [0 0]
  }
)

(defn update-state [state]
  (def s (* delta-time 3))
  (def v
    (cond
      (not (q/key-pressed?)) [0 0]
      (= (q/key-as-keyword) :a) [-1 0]
      (= (q/key-as-keyword) :d) [1 0]
      (= (q/key-as-keyword) :s) [0 -1]
      (= (q/key-as-keyword) :w) [0 1]
      true [0 0]
    )
  )
  (assoc {}
    :pos (xy/add (state :pos) (xy/mulf v s))
    :player (state :player)
  )
)

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

(defn draw-state [state]
  (q/background 240)
  (defn bx [] (q/no-stroke) (q/fill 150) (q/rect 0 0 1 1))
  (q/push-matrix)
  (apply q/apply-matrix (viewport (q/width) (q/height)))
  (apply q/apply-matrix (world 0 0 3))
  (sprite (state :player) (apply translation (state :pos)))
  (q/fill 255 0 0)
  (q/ellipse 0 0 0.1 0.1)
  (limb [0 0] 1 1 0 #() ;root
    [
      [
        [0 1] 0.4 0.6 0 bx ;body
        [
          [
            [0 0] 0.5 0.1 -120 bx ;arm-l
            [
              [
                [0.5 0] 0.5 0.1 0 bx ;forearm-l
              ]
            ]
          ]
        ]
      ]
    ]
  )
  (q/pop-matrix)
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