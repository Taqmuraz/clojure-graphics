(ns rotating-triangle.core
  (:require [quil.core :as q]))

(defn setup []
  (q/smooth)
  (q/frame-rate 60)
  (q/background 255)
  (q/stroke-weight 2))

(defn draw []
  (let [angle (* 0.05 (q/frame-count))
        x1 150
        y1 150
        x2 (+ 150 (* 50 (q/cos angle)))
        y2 (+ 150 (* 50 (q/sin angle)))
        x3 (+ 150 (* 50 (q/cos (+ angle (/ q/PI 2)))))
        y3 (+ 150 (* 50 (q/sin (+ angle (/ q/PI 2)))))]
    (q/background 255)
    (q/stroke 0)
    (q/fill 127)
    (q/triangle x1 y1 x2 y2 x3 y3)))

(defn -main []
  (q/defsketch rotating-triangle
    :size [300 300]
    :setup setup
    :draw draw))
