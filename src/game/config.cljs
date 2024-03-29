(ns game.config
  (:require [game.canvas :as c]
            [game.utils :as u]
            [game.constants :as C]))

;Moving patterns


(defn linear-move
  [object, t-diff]
  (let [{:keys [y time-to-travel pixels-to-travel]} object
        diff-factor  (/ t-diff time-to-travel)
        pixels-to-move (* diff-factor pixels-to-travel)
        new-y (+ y pixels-to-move)]
    (conj object
          {:y new-y
           :time-to-travel (- time-to-travel t-diff)
           :pixels-to-travel (- pixels-to-travel pixels-to-move)})))

;Falling Objects


(defprotocol FallingObject
  "Interface for falling objects"
  (moveObject [this t-diff] "Move for a single frame")
  (whenObjectCaught [this state] "Receives object that was caught and current state. Returns new game state")
  (whenObjectDrop [this state] "Receives object that was dropped and current state. Returns new game state"))

(defrecord Lambda [x y time-to-travel pixels-to-travel]
  FallingObject
  (moveObject [this t-diff] (linear-move this t-diff))
  (whenObjectCaught [_ state] (update-in state [:score] + C/GOOD-CATCH-SCORE-INCREASE))
  (whenObjectDrop [_ state] (update-in state [:lives] dec)))

(defrecord Bug [x y time-to-travel pixels-to-travel]
  FallingObject
  (moveObject [this t-diff] (linear-move this t-diff))
  (whenObjectCaught [_ state] (let [s (update-in state [:player :w] - C/PLAYER-WIDTH-CHANGE)]
                                (update-in s [:score] + C/BAD-CATCH-SCORE-INCREASE)))
  (whenObjectDrop [_ state] state))

(defrecord Rock [x y time-to-travel pixels-to-travel]
  FallingObject
  (moveObject [this t-diff] (linear-move this t-diff))
  (whenObjectCaught [_ state] (update-in state [:player :w] - C/PLAYER-WIDTH-CHANGE))
  (whenObjectDrop [_ state] state))

(defonce OBJECT-CONSTANTS {Lambda {:speed-range [1500 2500]
                                   :img C/LAMBDA-OBJECT-IMG
                                   :good? true}
                           Bug {:speed-range [1300 2000]
                                :img C/BUG-OBJECT-IMG
                                :good? false}
                           Rock {:speed-range [600 1300]
                                 :img C/ROCK-OBJECT-IMG
                                 :good? false}})

(defn- get-constant-for-type
  [constant type]
  (constant (get OBJECT-CONSTANTS type)))
(defonce constructors
  {Bug map->Bug
   Lambda map->Lambda
   Rock map->Rock})

(defn construct-object
  [object-type data]
  ((get constructors object-type) data))

(defn get-object-speed-range
  [object-type]
  (get-constant-for-type :speed-range object-type))

(defn get-object-image
  [object-type]
  (get-constant-for-type :img object-type))

(defn good-object?
  [object-type]
  (get-constant-for-type :good? object-type))

(defn create-object
  [{objects :objects speed-factor :speed-factor}]
  (let [object-type (u/pick-random-el-by-frequencies (keys objects) (vals objects))
        speed-range (get-object-speed-range object-type)
        [a b] speed-range
        base-speed (u/rand-interval a b)
        speed (- base-speed speed-factor)
        x (rand (- c/WIDTH C/OBJECT-SIZE))]

    (construct-object object-type {:x x
                                   :y 0
                                   :time-to-travel speed
                                   :pixels-to-travel c/HEIGHT})))

(defonce levels-config [{:objects {Lambda 10 Bug 15}
                         :speed-factor 0
                         :object-gen-interval [500 1000]}
                        {:objects {Bug 20 Lambda 20 Rock 5}
                         :speed-factor 100
                         :object-gen-interval [400 900]}
                        {:objects {Bug 20 Lambda 25 Rock 15}
                         :speed-factor 200
                         :object-gen-interval [300 800]}
                        {:objects {Bug 25 Lambda 30 Rock 20}
                         :speed-factor 300
                         :object-gen-interval [200 700]}
                        {:objects {Bug 30 Lambda 40 Rock 30}
                         :speed-factor 400
                         :object-gen-interval [100 600]}])

(defonce num-of-levels (count levels-config))

(defn get-level-config
  [level]
  (nth levels-config (dec level)))