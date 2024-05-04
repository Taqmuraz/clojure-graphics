(ns game.state
  (:require
    [linear.vector-xy :as xy]
    [game.input :as input]
    [game.time :as time]
  )
)

(defn make-state [state next] (merge state { :next next }))

(defn next-state [n] ((n :next) n))

(declare player-idle-state)
(declare player-walk-state)

(defn player-idle-state [s]
  (make-state
    (merge s { :anim (s :idle) })
    (fn [n]
      (cond
        (= (xy/len (input/wasd)) 0.0) n
        :else (player-walk-state n)
      )
    )
  )
)

(defn player-walk-state [s]
  (make-state
    (merge s { :anim (s :walk) })
    (fn [n]
      (def v (input/wasd))
      (cond
        (= (xy/len v) 0.0) (player-idle-state n)
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
