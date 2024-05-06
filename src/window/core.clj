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
  )
)

(defn setup []
  (q/frame-rate (time/fps))
  (q/color-mode :rgb)
  (def anims
    (reduce (fn [m [k v]]
      (conj m [k (anim/load-anim (q/load-image v) 512 512)]))
      {}
      [
        [:idle "res/knight_idle.png"]
        [:walk "res/knight_walk.png"]
      ]
    )
  )
  (gs/idle-state
    (merge
      anims
      {
        :anim (anims :idle)
        :pos [0 0]
        :dir 1
        :input input/wasd
      }
    )
  )
)

(defn update-state [state]
  (gs/next-state state)
)

(defn draw-state [state]
  (q/background 240)
  (q/no-stroke)
  (q/push-matrix)
  (apply q/apply-matrix (mat2/viewport (q/width) (q/height)))
  (apply q/apply-matrix (mat2/world 0 0 3))
  (draw/sprite ((state :anim) (time/time)) (apply mat2/translation-scale (concat (state :pos) [(* 2 (state :dir)) 2])))
  (q/fill 255 0 0)
  (q/ellipse 0 0 0.1 0.1)
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