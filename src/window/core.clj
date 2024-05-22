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
        [:attack "res/knight_attack.png"]
        [:dead "res/knight_dead.png"]
      ]
    )
  )
  (apply gs/list-state
    (->>
      (range 1)
      (map #(gs/idle-state
        (gs/agent anims :idle [(+ 0.5 (int (/ % 10))) (mod % 10)] -1 [2 2] (gs/empty-input)))
      )
      (cons 
        (gs/follow-by-camera
          (gs/idle-state
            (gs/agent anims :idle [0 0] 1 [2 2]
              (gs/make-input input/wasd (partial input/key-pressed? :f))
            )
          )
        )
      )
      (vec)
    )
  )
)

(defn update-state [state]
  (gs/next-state (gs/state-func state :effect) state)
)

(defn draw-state [state]
  (q/background 100 130 230)
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
  )
)