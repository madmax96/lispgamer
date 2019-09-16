(ns game.predicates
  (:require [game.canvas :as c]
            [game.utils :as u]
            [game.config :as config]
            [game.constants :as C]))

(defn game-over?
  [{lives :lives}]
  (zero? lives))

(defn level-completed?
  [{:keys [falling-objects level-state] :as state}]
  (and (zero? (count falling-objects))
       (zero? (count (keys (:objects level-state))))
       (not (game-over? state))))

(defn game-completed?
  [{current-level :current-level :as state}]
  (and
   (level-completed? state)
   (= current-level config/num-of-levels)))

(defn playing?
  [{started-game? :started-game? :as state}]
  (and
   (not (game-over? state))
   (not (level-completed? state))
   started-game?))

(defn object-lost?
  [object {player :player}]
  (let [{player-x :x w :w} player {obj-x :x} object]
    (not (and
          (< player-x obj-x)
          (>= (+ w player-x) (+ obj-x C/OBJECT-SIZE))))))

(defn object-caught?
  [object state]
  (not (object-lost? object state)))

(defn object-at-boundary?
  [{y :y} {player :player}]
  (> (+ y C/OBJECT-SIZE) (- c/HEIGHT (:h player))))

(defn create-object?
  [t {:keys [level-state last-object-created-timestamp]}]
  (let [{[a b] :object-gen-interval objects :objects} level-state
        object-types (keys objects)]
    (and
     object-types
     (or
      (zero? last-object-created-timestamp)
      (> (- t last-object-created-timestamp)
         (u/rand-interval a b))))))