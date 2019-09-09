(ns game.state
  (:require [game.canvas :as c]
            [game.utils :as u]
            [game.config :as config]
            [reagent.core :as r]
            )
  )
;Game Initial State
(defonce started-game? (atom false))
(defonce current-level (r/atom 1))
(defonce score (r/atom 0))
(defonce lives (r/atom config/INITIAL-LIVES))
(defonce level-state (r/atom {}))
(defonce falling-objects (atom []))
(defonce last-object-created-timestamp (atom 0))
(defonce last-timestamp (atom 0))
(defonce initial-player {
                         :x 0
                         :y (- c/HEIGHT config/PLAYER-HEIGHT)
                         :h config/PLAYER-HEIGHT
                         :w config/PLAYER-WIDTH
                         :img (.getElementById js/document "playerImage")
                         })
(defonce paused? (atom false))

(defn toggle-pause
  []
  (reset! last-timestamp (.now js/performance))
  (reset! paused? (not @paused?))
  )

(defn continue
  []
  (reset! paused? false)
  )

(defonce player (atom initial-player))

(defn update-player-width
  [num]
  (swap! player update :w + num)
  )

(defn set-falling-objects
  [objects]
    (reset! falling-objects objects)
  )

(defn move-player
  [x]
  (swap! player conj {:x x})
  )

(defn decrement-lives
  []
  (swap! lives dec)
  )

(defn set-last-timestamp
  [t]
  (reset! last-timestamp t)
  )

(defn set-last-object-created-timestamp
  [t]
  (reset! last-object-created-timestamp t)
  )

(defn update-score
  [num]
  (swap! score + num)
  )

;predicates
(defn game-over?
  []
  (= 0 @lives)
  )

(defn level-completed?
  []
  (println "LEVEL COMPLETED "  (and (= 0 (count @falling-objects))
                                    (= 0 (count (keys (:objects @level-state))))
                                    ))
  (and (= 0 (count @falling-objects))
       (= 0 (count (keys (:objects @level-state))))
       )
  )

(defn game-completed?
  []
  (println  "GAME COMPLETED " (= @current-level config/num-of-levels))

  (and
    (level-completed?)
    (= @current-level config/num-of-levels)
    )
  )
(defn playing?
  []
  (and
    (not (game-over?))
    (not (level-completed?))
    @started-game?)
  )
(defn object-lost?
  [object]
  (let [{player-x :x w :w } @player {obj-x :x } object]
     (not (and
            (< player-x obj-x)
            (>= (+ w player-x) (+ obj-x config/OBJECT-SIZE))
            )
          )
    )
  )

(defn object-caught?
  [object]
  (not (object-lost? object))
  )

(defn object-at-boundary?
  [{y :y}]
  (> (+ y config/OBJECT-SIZE) (- c/HEIGHT (:h @player)))
  )

(defn create-object?
  [t]
  (let [{ [a b] :object-gen-interval objects :objects} @level-state
        object-types (keys objects)
        ]
    (and
      object-types
      (or
           (= @last-object-created-timestamp 0)
           (> (- t @last-object-created-timestamp)
              (u/rand-interval a b))
           )
      )
    )
  )

(defn next-level
  []
  (if (not (game-completed?))
    (swap! current-level inc)
    )
  )

(defn- _create-object
  [{objects :objects speed-factor :speed-factor}]
  (let [
        object-types (keys objects)
        object-type (u/pick-random-el object-types)
        {:keys [speed-range img]} (object-type config/falling-objects-config)
        [a b] speed-range
        base-speed (u/rand-interval a b)
        speed (- base-speed speed-factor)
        ]
    {
     :type object-type
     :x (rand (- c/WIDTH config/OBJECT-SIZE))
     :y 0
     :img img
     :time-to-travel speed
     :pixels-to-travel c/HEIGHT
     }
    )
  )

(defn create-object
  [t]
    (let [l-state @level-state
          new-object (_create-object l-state)
          object-type (:type new-object)
          objects (:objects l-state)
          ]
      (swap! falling-objects conj new-object)
      (if (= 1 (object-type objects))
        (swap! level-state update-in [:objects] dissoc object-type)
        (swap! level-state update-in [:objects] update object-type dec)
        )
      (set-last-object-created-timestamp t)
      new-object
      )
  )

(defn init-level-state
  []
  (reset! last-object-created-timestamp 0)
  (reset! last-timestamp 0)
  (reset! level-state (config/get-level-config @current-level))
  (reset! falling-objects [])
  )

(defn reset
  "resets game to initial state"
  []
  (reset! lives config/INITIAL-LIVES)
  (reset! score 0)
  (reset! current-level 1)
  (reset! player initial-player)
  (reset! started-game? false)
  (init-level-state)
  )