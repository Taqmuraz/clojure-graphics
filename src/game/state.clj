(ns game.state
  (:require
    [linear.vector-xy :as xy]
    [game.time :as time]
  )
)

(defn make-state [state next] (merge state { :next next }))

(defn next-state [n] ((n :next) n))

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
