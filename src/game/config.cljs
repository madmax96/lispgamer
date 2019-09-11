(ns game.config
  (:require [game.canvas :as c]
            [game.utils :as u]
            [game.constants :as C]
            )
  )

;Moving patterns
(defn linear-move
  [object, t-diff]
  (let [{:keys [y time-to-travel pixels-to-travel]} object
        diff-factor  (/ t-diff time-to-travel)
        pixels-to-move (* diff-factor pixels-to-travel)
        new-y (+ y pixels-to-move)
        ]
    (conj object
          {
           :y new-y
           :time-to-travel (- time-to-travel t-diff)
           :pixels-to-travel (- pixels-to-travel pixels-to-move)
           }
          )
    )
  )

;Falling Objects
(defprotocol FallingObject
  "Interface for falling objects"
  (moveObject [this t-diff] "Move for a single frame")
  (whenObjectCaught [this state] "Receives object that was caught and current state. Returns new game state")
  (whenObjectDrop [this state] "Receives object that was dropped and current state. Returns new game state")
  )

(defrecord Lambda [x y time-to-travel pixels-to-travel]
  FallingObject
  (moveObject [this t-diff] (linear-move this t-diff))
  (whenObjectCaught [_ state] (update-in state [:score] + C/GOOD-CATCH-SCORE-INCREASE))
  (whenObjectDrop [_ state] (update-in state [:lives] dec))
  )

(defrecord Bug [x y time-to-travel pixels-to-travel]
  FallingObject
  (moveObject [this t-diff] (linear-move this t-diff))
  (whenObjectCaught [_ state] (let [s (update-in state [:player :w] - C/PLAYER-WIDTH-CHANGE)]
                                      (update-in s [:score] + C/BAD-CATCH-SCORE-INCREASE)
                                   ))
  (whenObjectDrop [_ state] state)
  )

(defrecord Rock [x y time-to-travel pixels-to-travel]
  FallingObject
  (moveObject [this t-diff] (linear-move this t-diff))
  (whenObjectCaught [_ state] (update-in state [:player :w] - C/PLAYER-WIDTH-CHANGE))
  (whenObjectDrop [_ state] state)
  )

(defonce OBJECT-CONSTANTS {
                           Lambda {
                                   :speed-range [1300 2200]
                                   :img C/LAMBDA-OBJECT-IMG
                                   :good? true
                                   }
                           Bug {
                                :speed-range [900 1500]
                                :img C/BUG-OBJECT-IMG
                                :good? false
                                }
                           Rock {
                                 :speed-range [500 700]
                                 :img C/ROCK-OBJECT-IMG
                                 :good? false
                                 }
                           })
(defonce constructors
         {
          Bug map->Bug
          Lambda map->Lambda
          Rock map->Rock
          }
         )

(defn- get-constant-for-type
  [constant type]
  (constant (get OBJECT-CONSTANTS type))
  )
(defn construct-object
  [object-type data]
  (println object-type data)
  ((get constructors object-type) data)
  )
(defn get-object-speed-range
  [object-type]
  (get-constant-for-type :speed-range object-type)
  )

(defn get-object-image
  [object-type]
  (get-constant-for-type :img object-type)
  )

(defn good-object?
  [object-type]
  (get-constant-for-type :good? object-type)
  )

(defn create-object
  [{objects :objects speed-factor :speed-factor}]
  (let [
        object-types (keys objects)
        object-type (u/pick-random-el object-types)
        speed-range (get-object-speed-range object-type)
        [a b] speed-range
        base-speed (u/rand-interval a b)
        speed (- base-speed speed-factor)
        x (rand (- c/WIDTH C/OBJECT-SIZE))
        ]

    (construct-object object-type {:x x
                                   :y 0
                                   :time-to-travel speed
                                   :pixels-to-travel c/HEIGHT
                                  })
    )
  )

(defonce levels-config [
                        {
                         :objects {Bug 1 Lambda 1}
                         :speed-factor 0
                         :object-gen-interval [1200 1800]
                         }
                        {
                         :objects {Bug 1 Lambda 1 Rock 5}
                         :speed-factor 100
                         :object-gen-interval [900 1500]
                         }
                        {
                         :objects {Bug 2 Lambda 2 Rock 2}
                         :speed-factor 200
                         :object-gen-interval [400 1200]
                         }
                        {
                         :objects {Bug 2 Lambda 2 Rock 2}
                         :speed-factor 300
                         :object-gen-interval [300 700]
                         }
                        ])

(defonce num-of-levels (count levels-config))

(defn get-level-config
  [level]
  (nth levels-config (- level 1))
  )