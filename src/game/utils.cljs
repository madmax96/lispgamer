(ns game.utils)


(defn rand-interval
  [a b]
  (+ a ((comp rand-int inc -) b a))
  )

(defn pick-random-el
  [coll]
  (let [c (count coll) i (rand-interval 0 (- c 1))]
    (nth coll i)
    )
  )

(defn play-sound
  "plays sound that corresponds to an event"
  [sound]
  (try
    ()
    (set! (.-currentTime sound) 0)
    (.play sound)
    (catch js/Object e
      (.log js/console "Sound could not be played")))
  )

