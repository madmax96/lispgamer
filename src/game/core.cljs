(ns game.core
    (:require [game.canvas :as c]
              [game.utils :as u]
              [game.events :as e]
              [game.config :as config]
              [game.state :as state]
              [reagent.core :as r]
              )
    )

(enable-console-print!)

(defonce PLAY-BUTTON (.getElementById js/document "play"))
(defonce NEXT-LEVEL-BUTTON (.getElementById js/document "nextLevel"))

(defn filter-and-handle
    [obj]
    (if (state/object-at-boundary? obj)
        (if (state/object-lost? obj)
            (do
                (e/handle-object-lost obj)
                false
                )
            (do
                (e/handle-object-caught obj)
                false
                )
            )
        true
        )
    )

(defn update-object
    [obj t-diff]
    (let [{:keys [y time-to-travel pixels-to-travel]} obj
          diff-factor  (/ t-diff time-to-travel)
          pixels-to-move (* diff-factor pixels-to-travel)
          new-y (+ y pixels-to-move)
          ]
        (conj obj
              {
               :y new-y
               :time-to-travel (- time-to-travel t-diff)
               :pixels-to-travel (- pixels-to-travel pixels-to-move)
               }
              )
        )
    )

(defn update-falling-objects
    [t]
    (let [time-difference (- t @state/last-timestamp)]
        (println "t diff" time-difference)
            ;filter falling objects and emmit corresponding events
            (state/set-falling-objects
                (map
                    (fn [obj]
                        (update-object obj time-difference))
                    (filter filter-and-handle @state/falling-objects)
                    )
                )
        )
    (if (state/create-object? t)
        (let [new-object (state/create-object t)]
            (e/handle-object-created new-object)
            )
        )
    )

;Drawing
(defn draw-player
    []
    (let [{ x :x y :y w :w h :h img :img } @state/player]
        (c/draw-image img x y w h)
        )
    )

(defn draw-falling-objects
    [t]
    (update-falling-objects t)
    (doseq [{:keys [img x y]} @state/falling-objects]
        (c/draw-image img x y config/OBJECT-SIZE config/OBJECT-SIZE)
        )
    )

(defn draw-frame
    "draws single frame"
    [t]
    (cond
        (state/level-completed?)
            (do
                (c/clear-canvas)
                (c/draw-background)
                (state/next-level)
                )
        (state/game-over?)
           (do
               (c/clear-canvas)
               (c/draw-background))
        :else
              (if (not @state/paused?)
                  (do
                      (c/clear-canvas)
                      (c/draw-background)
                      (draw-player)
                      (draw-falling-objects t)
                      (state/set-last-timestamp t)
                      (.requestAnimationFrame js/window draw-frame)
                      )
                  )
        )
    )

(defn calculate-player-x
    [offset-x]
    (let [ {w :w} @state/player]
        (.min js/Math (.max js/Math 0 (- offset-x (/ w 2))) (- c/WIDTH w))
        )
    )

(defn start-level
    []
    (if (state/game-over? )
        (state/reset)
        )
    (set! (.-onmousemove c/canvas) #(state/move-player (calculate-player-x (.-offsetX %))))
    (reset! state/started-game? true)
    (state/init-level-state)
    (.requestAnimationFrame js/window draw-frame)
    )

(c/draw-background)
(state/init-level-state)
(set! (.-onkeydown js/document) (fn [e]
                                  (if (and
                                          (state/playing?)
                                          (= (.-keyCode e) 32)
                                          )
                                      (do
                                          (state/toggle-pause)
                                          (if @state/paused?
                                              ()
                                              (draw-frame (+ 1 @state/last-timestamp))
                                              )
                                          )
                                      )
                                  ))
;UI
(defn game-ui []
    (let [lives @state/lives
          level @state/current-level
          score @state/score
          _ @state/level-state ; just to make sure ui component re-renders so it knows when level is over
          game-over? (state/game-over?)
          game-completed? (state/game-completed?)
          level-completed? (state/level-completed?)
          playing? (and (not game-over?) (not level-completed?) @state/started-game?)
          ]
        (println playing?)
        (cond
            playing? (def menu nil)
            game-over? (def menu [:div.menu
                                  [:p.game-over "Game over"]
                                  [:button {:on-click #(state/reset)} "Try Again"]
                                  ])
            game-completed? (def menu [:div.menu
                                  [:p.game-completed "Congratulations!!! Game Completed"]
                                  [:button {:on-click #(state/reset)} "Play Again"]
                                  ])
            level-completed? (def menu [:div.menu
                                        [:p.level-completed "Level Completed"]
                                        [:button {:on-click start-level} "Next Level"]
                                        ])
            :else (def menu [:div.menu
                              [:button {:on-click start-level} "Play"]
                              [:button "Instructions"]
                            ])
            )
        [:div.ui-container
         [:div.game-info
          [:p "Lives: " lives]
          [:p "Score: " score]
          [:p "Level: " level]]
            menu
         ]
        )
    )

(r/render [game-ui] (.getElementById js/document "ui"))

