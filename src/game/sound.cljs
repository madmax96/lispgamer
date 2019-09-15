(ns game.sound
  (:require [game.utils :as u]
            [game.config :as config]
            [game.constants :as C]))

(defonce events-sound-map {:GOOD-CATCH  C/GOOD-CATCH-SOUND
                           :BAD-CATCH C/BAD-CATCH-SOUND
                           :BAD-DROP C/BAD-DROP-SOUND
                           :OBJECT-CREATED C/OBJECT-CREATED-SOUND
                           :GAME-OVER C/GAME-OVER-SOUND
                           :LEVEL-COMPLETED C/LEVEL-COMPLETED-SOUND})

(defn object-lost
  [object]
  (if (config/good-object? (type object))
    (u/play-sound (:BAD-DROP events-sound-map))))

(defn object-caught
  [object]
  (if (config/good-object? (type object))
    (u/play-sound (:GOOD-CATCH events-sound-map))
    (u/play-sound (:BAD-CATCH events-sound-map))))

(defn level-completed
  []
  (u/play-sound (:LEVEL-COMPLETED events-sound-map)))

(defn game-over
  []
  (u/play-sound (:GAME-OVER events-sound-map)))

(defn object-created
  []
  (u/play-sound (:OBJECT-CREATED events-sound-map)))