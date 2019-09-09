(ns game.events
  (:require [game.state :as state]
            [game.utils :as u]
            [game.canvas :as c]
            [game.config :refer [falling-objects-config]]
            )
  )

(defonce objects-events-config
         {
          :lost {
                 :bug #()
                 :lambda #(state/decrement-lives)
                 }

          :caught {
                   :bug #(do
                           (state/update-score -10)
                           (state/update-player-width -3))
                   :lambda #(state/update-score 5)
                   :rock #(state/update-player-width -15)
                   }
          }
         )
(defonce events-sound-map {
                           :GOOD-LOST ""
                           :GOOD-CAUGHT  (.getElementById js/document "goodCaught")
                           :BAD-CAUGHT ""
                           :OBJECT-CREATED ""
                           :GAME-OVER ""
                           :LEVEL-COMPLETED ""
                           })

(defn handle-object-lost
  [object]
  ;(if (:good? object)
  ;  ;(u/play-sound (:GOOD-LOST events-sound-map))
  ;  ;(u/play-sound (:BAD-LOST events-sound-map))
  ;  )
  (let [{type :type} object handler (type (:lost objects-events-config))]
    (if handler (handler))
    )
  )
(defn handle-level-completed
  []
  (u/play-sound (:LEVEL-COMPLETED events-sound-map))
  )

(defn handle-game-over
  []
  (u/play-sound (:GAME-OVER events-sound-map))
  )

(defn handle-object-caught
  [object]
  (if (:good? ((:type object) falling-objects-config))
    (u/play-sound (:GOOD-CAUGHT events-sound-map))
    ;(u/play-sound (:BAD-CAUGHT events-sound-map))
    )
  (let [{type :type} object handler (type (:caught objects-events-config))]
    (if handler (handler))
    )
  )

(defn handle-object-created
  [object]
  ;(u/play-sound (:OBJECT-CREATED events-sound-map))
  )