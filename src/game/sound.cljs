(ns game.sound
  (:require [game.utils :as u]
            [game.config :as config]
            [game.constants :as C]
            )
  )

(defonce events-sound-map {
                           :GOOD-LOST ""
                           :GOOD-CAUGHT  C/GOOD-CATCH-SOUND
                           :BAD-CAUGHT ""
                           :OBJECT-CREATED ""
                           :GAME-OVER ""
                           :LEVEL-COMPLETED ""
                           })

(defn object-lost
  [object]
  ;(if (config/good-object? (type object))
  ;  (u/play-sound (:GOOD-LOST game.sound-sound-map))
  ;  (u/play-sound (:BAD-LOST game.sound-sound-map))
  ;  )
  )

(defn object-caught
  [object]
  (if (config/good-object? (type object))
    (u/play-sound (:GOOD-CAUGHT events-sound-map))
    ;(u/play-sound (:BAD-CAUGHT game.sound-sound-map))
    )
  )

(defn level-completed
  []
  (u/play-sound (:LEVEL-COMPLETED events-sound-map))
  )

(defn game-over
  []
  (u/play-sound (:GAME-OVER events-sound-map))
  )

(defn object-created
  [object]
  ;(u/play-sound (:OBJECT-CREATED game.sound-sound-map))
  )