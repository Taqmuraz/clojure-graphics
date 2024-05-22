(ns game.state
  (:require
    [linear.vector-xy :as xy]
    [linear.rect :as rect]
    [linear.matrix-2x3 :as mat2]
    [game.time :as time]
    [game.draw :as draw]
    [game.anim :as anim]
  )
)

(defn state-func
  ([state sym] ((state sym) state))
  ([state sym arg] ((state sym) arg state))
)

(defn effect-identity [s] {})

(defn make-state
  ([state next] (make-state state next effect-identity))
  ([state next effect] (merge state { :effect effect :next next }))
)

(defn next-state [eff n] (state-func n :next eff))

(defn draw-state [d] (state-func d :draw))

(defn follow-by-camera [s]
  (assoc s :cam-pos (fn [n] (n :pos)))
)

(defn camera-from-state [s] ((get s :cam-pos (fn [_] [0 0])) s))

(defn make-input [walk attack] { :walk walk :attack attack })
(defn empty-input [] { :walk (constantly [0 0]) :attack (constantly false)})

(defn agent [anims anim pos dir scale input]
  (merge anims
    {
      :tag (random-uuid)
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
  :effect
  (fn [s]
    (
      (comp (partial apply (partial merge-with concat)) map)
      #(state-func % :effect)
      (s :states)
    )
  )
  :next
  (fn [eff n]
    (apply list-state
      (map (partial next-state eff) (n :states))
    )
  )
})

(def same-tag? =)

(defn read-input [state sym] (((state :input) sym)))

(defn rect-from-state [s]
  ((comp vec concat) (s :pos) [1 1])
)

(defn attack-rect [s]
  ((comp vec concat) (xy/add (s :pos) [(* 0.5 (s :dir)) 0]) [0.5 0.5])
)

(declare idle-state)
(declare walk-state)
(declare attack-state)
(declare dead-state)

(defn check-attack [eff s]
  (
    (comp
      not
      empty?
      (partial filter (partial rect/intersect? (rect-from-state s)))
      (partial map :rect)
      (partial filter #((complement same-tag?) (s :tag) (% :source)))
    )
    (eff :attack)
  )
)

(defn dead-state [s] 
  (make-state
    (assoc s :anim (s :dead))
    (fn [eff n] n)
  )
)

(defn idle-state [s]
  (make-state
    (assoc s :anim (s :idle))
    (fn [eff n]
      (cond
        (check-attack eff n) (dead-state n)
        (read-input n :attack) (attack-state n)
        (= (xy/len (read-input n :walk)) 0.0) n
        :else (walk-state n)
      )
    )
  )
)

(defn attack-state [s]
  (def start (time/time))
  (make-state
    (assoc s
      :anim (anim/anim-offset (s :attack) start)
    )
    (fn [eff n]
      (cond
        (check-attack eff n) (dead-state n)
        (> (- (time/time) start) 1) (idle-state n)
        :else n
      )
    )
    (fn [s]
      (cond
        ((every-pred (partial < 0.4) (partial > 0.7)) (- (time/time) start))
        {
          :attack
          [{
            :source (s :tag)
            :damage 1
            :rect (attack-rect s)
          }]
        }
        :else {}
      )
    )
  )
)

(defn walk-state [s]
  (make-state
    (assoc s :anim (s :walk))
    (fn [eff n]
      (def v (read-input n :walk))
      (cond
        (check-attack eff n) (dead-state n)
        (read-input n :attack) (attack-state n)
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
