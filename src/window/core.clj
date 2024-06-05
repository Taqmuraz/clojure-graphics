(ns window.core
  (:require
    [quil.core :as q]
    [quil.middleware :as m]
    [linear.vector-xy :as xy]
    [linear.matrix-2x3 :as mat2]
    [game.state :as gs]
    [game.time :as time]
    [game.anim :as anim]
    [game.draw :as draw]
    [game.input :as input]
    [game.skeletal :as sk]
  )
  (:gen-class)
)

(defn setup []
  (q/frame-rate (time/fps))
  (q/color-mode :rgb)
  {
    :papa (q/load-image "res/human-map.png")
    :next (fn [eff n] n)
    :effect gs/effect-identity
    :draw (fn [d] ())
  }
)

(defn update-state [state]
  (gs/next-state (gs/state-func state :effect) state)
)

(defn draw-state [state]
  (q/background 230 230 230)
  (q/no-stroke)
  (q/push-matrix)
  (apply q/apply-matrix (mat2/viewport (q/width) (q/height)))
  (apply q/apply-matrix
    (apply mat2/ortho
      (concat
        (xy/sub (gs/camera-from-state state))
        [3 (/ (q/width) (q/height))]
      )
    )
  )
  (gs/draw-state state)
  (q/fill 255 0 0)
  (q/ellipse 0 0 0.1 0.1)

  (let [
      papa (state :papa)
      HPI (/ clojure.math/PI 2)
      ; layout : image uv pos pivot scale rotation & children
      head (sk/limb papa  [0.8 0 0.2 0.33]  [0 0.6]  [0.5 0.25] [0.2 0.2] 0)
      kneel (sk/limb papa [0.4 0.5 0.2 0.5] [0 0.4]  [0.5 0.25] [0.2 0.4] 0)
      kneer (sk/limb papa [0.4 0.5 0.2 0.5] [0 0.4]  [0.5 0.25] [0.2 0.4] 0)
      legl (sk/limb papa  [0.4 0 0.2 0.5]   [-0.1 0] [0.5 0.25] [0.2 0.5] (* HPI 1.8) kneel)
      legr (sk/limb papa  [0.4 0 0.2 0.5]   [0.1 0]  [0.5 0.25] [0.2 0.5] (* HPI 2.2))
      limbs (sk/limb papa [0 0 0.4 1]       [0 1]    [0.5 0]    [0.4 0.6] 0 head legl legr)
    ]
    (limbs draw/sprite)
  )

  (q/pop-matrix)
)

(defn -main [& args]
  (q/defsketch window
    :title "Lis'p"
    :size [800 600]
    :renderer :p3d
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]
    :key-pressed input/key-pressed
    :key-released input/key-released
  )
)