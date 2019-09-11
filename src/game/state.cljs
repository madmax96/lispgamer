(ns game.state
  (:require [game.canvas :as c]
            [game.constants :as C]
            [game.config :as config]
            [reagent.core :as r]
            )
  )
;Game Initial State
(defonce started-game? (atom false))
(defonce current-level (r/atom 1))
(defonce score (r/atom 0))
(defonce lives (r/atom C/INITIAL-LIVES))
(defonce level-state (r/atom {}))
(defonce falling-objects (atom []))
(defonce last-object-created-timestamp (atom 0))
(defonce last-timestamp (atom 0))
(defonce initial-player {
                         :x 0
                         :y (- c/HEIGHT C/PLAYER-HEIGHT)
                         :h C/PLAYER-HEIGHT
                         :w C/PLAYER-WIDTH
                         :img C/PLAYER-IMG
                         })

(defonce player (atom initial-player))
(defonce paused? (atom false))


(defn get-state
  []
  {
   :started-game? @started-game?
   :current-level @current-level
   :score @score
   :lives @lives
   :level-state @level-state
   :falling-objects @falling-objects
   :last-object-created-timestamp @last-object-created-timestamp
   :last-timestamp @last-timestamp
   :player @player
   :paused? @paused?
   }
  )

(defn set-state
  [{sg :started-game? cr :current-level scr :score l :lives
           lst :level-state fo :falling-objects loct :last-object-created-timestamp
           lt :last-timestamp p :player paused :paused?}]
    (reset! started-game? sg)
    (reset! current-level cr)
    (reset! score scr)
    (reset! lives l)
    (reset! level-state lst)
    (reset! falling-objects fo)
    (reset! player p)
  nil
  )

(defn toggle-pause
  []
  (reset! last-timestamp (.now js/performance))
  (reset! paused? (not @paused?))
  )

(defn set-falling-objects
  [objects]
    (reset! falling-objects objects)
  )

(defn move-player
  [x]
  (swap! player conj {:x x})
  )

(defn set-last-timestamp
  [t]
  (reset! last-timestamp t)
  )

(defn set-last-object-created-timestamp
  [t]
  (reset! last-object-created-timestamp t)
  )

(defn next-level
  []
    (swap! current-level inc)
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
  (reset! lives C/INITIAL-LIVES)
  (reset! score 0)
  (reset! current-level 1)
  (reset! player initial-player)
  (reset! started-game? false)
  (init-level-state)
  )