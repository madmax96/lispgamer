(ns game.utils)

(defn rand-interval
  [a b]
  (+ a ((comp rand-int inc -) b a)))

(defn pick-random-el
  [coll]
  (let [c (count coll) i (rand-interval 0 (dec c))]
    (nth coll i)))

(defn pick-random-el-by-frequencies
  [elements frequencies]
  (let [freq-sum (reduce + frequencies)
        probabilities (map #(/ % freq-sum) frequencies)
        cumulative-probability (reductions + probabilities)
        r (rand)
        i (.indexOf cumulative-probability (first (filter #(< r %) cumulative-probability)))]
    (nth elements i)))

(defn play-sound
  "plays sound that corresponds to an event"
  [sound]
  (try
    ()
    (set! (.-currentTime sound) 0)
    (.play sound)
    (catch js/Object e
      (.log js/console "Sound could not be played"))))

