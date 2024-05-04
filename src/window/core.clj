(ns window.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [linear.vector-xy :as xy]))

(def fps 60)
(def delta-time (/ 1.0 60.0))

(defn sprite [img mat]
  (q/push-matrix)
  (apply q/apply-matrix mat)
  (q/translate -0.5 0.5)
  (q/scale 1 -1)
  (q/image img 0 0 1 1)
  (q/pop-matrix)
)

(defn time[]
  (* (q/millis) 0.001)
)

(defn anim [fs n]
  (fn [t]
    (fs (int (mod (* t n) n)))
  )
)

(defn load-anim [img ew eh]
  (while (not (q/loaded? img)) ())
  (def size [(.width img) (.height img)])
  (def num (/ (size 0) ew))
  (def res
    (vec
      (map
        #(do [% (q/create-image ew eh :argb)])
        (range num)
      )
    )
  )
  (doseq [[i frame] res]
    (q/copy
      img
      frame
      [(* ew i) 0 ew eh]
      [0 0 ew eh]))
  (anim (vec (map xy/get-y res)) num)
)

(declare player-idle-state)
(declare player-walk-state)

(defn setup []
  (q/frame-rate fps)
  (q/color-mode :rgb)
  (def anims
    (reduce (fn [m [k v]]
      (conj m [k (load-anim (q/load-image v) 512 512)]))
      {}
      [
        [:idle "res/knight_idle.png"]
        [:walk "res/knight_walk.png"]
      ]
    )
  )
  (player-idle-state
    (merge
      anims
      {
        :anim (anims :idle)
        :pos [0 0]
        :dir 1
      }
    )
  )
)

(defn wasd []
  (cond
    (not (q/key-pressed?)) [0 0]
    (= (q/key-as-keyword) :a) [-1 0]
    (= (q/key-as-keyword) :d) [1 0]
    (= (q/key-as-keyword) :s) [0 -1]
    (= (q/key-as-keyword) :w) [0 1]
    :else [0 0]
  )
)

(defn make-state [state next] (merge state { :next next }))

(defn next-state [n] ((n :next) n))

(defn player-idle-state [s]
  (make-state
    (merge s { :anim (s :idle) })
    (fn [n]
      (cond
        (= (xy/len (wasd)) 0.0) n
        :else (player-walk-state n)
      )
    )
  )
)

(defn player-walk-state [s]
  (make-state
    (merge s { :anim (s :walk) })
    (fn [n]
      (cond
        (= (xy/len (wasd)) 0.0) (player-idle-state n)
        :else
        (do
          (def sp (* delta-time 3))
          (def v (wasd))
          (def dx (double(v 0)))
          (merge n
            {
              :pos (xy/add (n :pos) (xy/mulf v sp))
              :dir
              (cond
                (> dx 0) 1
                (< dx 0) -1
                :else (n :dir)
              )
            }
          )
        )
      )
    )
  )
)

(defn update-state [state]
  (next-state state)
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

(defn translation-scale [x y w h]
  [
    w 0 x
    0 h y
  ]
)

(defn draw-state [state]
  (q/background 240)
  (q/no-stroke)
  (defn bx [] (q/no-stroke) (q/fill 150) (q/rect 0 0 1 1))
  (q/push-matrix)
  (apply q/apply-matrix (viewport (q/width) (q/height)))
  (apply q/apply-matrix (world 0 0 3))
  (sprite ((state :anim) (time)) (apply translation-scale (concat (state :pos) [(* 2 (state :dir)) 2])))
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