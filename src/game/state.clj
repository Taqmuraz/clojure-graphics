(ns game.state
  (:require
    [linear.vector-xy :as xy]
    [linear.matrix-2x3 :as mat2]
    [game.time :as time]
    [game.draw :as draw]
  )
)

(defn make-state [state next] (merge state { :next next }))

(defn next-state [n] ((n :next) n))

(defn draw-state [d] ((d :draw) d))

(defn follow-by-camera [s]
  (assoc s :cam-pos (fn [n] (n :pos)))
)

(defn camera-from-state [s] ((get s :cam-pos (fn [_] [0 0])) s))

(defn agent [anims anim pos dir scale input]
  (merge anims
    {
      :anim (anims anim)
      :draw
      (fn [d]
        (draw/sprite
          ((d :anim) (time/time))
          (apply mat2/translation-scale
            (concat
              (d :pos)
              (xy/mul scale [(d :dir) 1])
            )
          )
        )
      )
      :pos pos
      :dir dir
      :input input
    }
  )
)

(defn list-state [& states]
{
  :states states
  :cam-pos (fn [s] (apply xy/add (map camera-from-state (s :states))))
  :draw
  (fn [d]
    (doseq [s (d :states)]
      (draw-state s)
    )
  )
  :next
  (fn [n]
    (apply list-state
      (map next-state (n :states))
    )
  )
})

(declare idle-state)
(declare walk-state)

(defn idle-state [s]
  (make-state
    (merge s { :anim (s :idle) })
    (fn [n]
      (cond
        (= (xy/len ((n :input))) 0.0) n
        :else (walk-state n)
      )
    )
  )
)

(defn walk-state [s]
  (make-state
    (merge s { :anim (s :walk) })
    (fn [n]
      (def v ((n :input)))
      (cond
        (= (xy/len v) 0.0) (idle-state n)
        :else
        (do
          (def sp (* (time/delta-time) 3))
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
