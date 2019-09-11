(ns game.core
    (:require [game.canvas :as c]
              [game.utils :as u]
              [game.sound :as s]
              [game.config :as config]
              [game.state :as state]
              [reagent.core :as r]
              [game.predicates :as pred]
              [game.constants :as C]
              )
    )

(enable-console-print!)

;(defn create-object
;    [t {level-state :level-state :as state}]
;    (let [new-object (config/create-object level-state)
;          object-type (type new-object)
;          objects (:objects level-state)
;          new-state (update-in state [:falling-objects] conj new-object)
;          ]
;
;        (if (= 1 (get objects object-type))
;            (swap! state/level-state update-in [:objects] dissoc object-type)
;            (swap! state/level-state update-in [:objects] update object-type dec)
;            )
;        (state/set-last-object-created-timestamp t)
;        new-object
;        )
;    )

;(defn filter-and-handle
;    [obj]
;    (if (pred/object-at-boundary? obj (state/get-state))
;        (if (pred/object-lost? obj (state/get-state))
;            (do
;                (state/set-state (config/whenObjectDrop obj (state/get-state)))
;                (s/object-lost obj)
;                false
;                )
;            (do
;                (state/set-state (config/whenObjectCaught obj (state/get-state)))
;                (s/object-caught obj)
;                false
;                )
;            )
;        true
;        )
;    )
;TODO Refactor game playing? paused? ... can i use one variable ?
(defn update-falling-objects
    [t {:keys [last-timestamp falling-objects level-state] :as state}]
    (let [time-difference (- t last-timestamp)
          {at-boundary true safe false} (group-by #(pred/object-at-boundary? % state) falling-objects)
          {droped true caught false} (group-by #(pred/object-lost? % state) at-boundary)
          moved-falling-objects  (map
                                        (fn [obj]
                                            (config/moveObject obj time-difference))
                                        safe
                                        )
          ;Update all state that needs to be updated
          ]
        (if (pred/create-object? t state)
            (let [new-object (config/create-object level-state)]
                (s/object-created new-object)
                ;(conj )
                )
            )
        )
    )

;Drawing
(defn draw-player
    [{player :player}]
    (let [{ x :x y :y w :w h :h img :img } player]
        (c/draw-image img x y w h)
        )
    nil
    )

(defn draw-falling-objects
    [{falling-objects :falling-objects}]
    (doseq [{:keys [x y] :as O} falling-objects]
        (c/draw-image (config/get-object-image (type O)) x y C/OBJECT-SIZE C/OBJECT-SIZE)
        )
    nil
    )

(defn draw-frame
    "draws single frame"
    [t]
    (let [state (state/get-state)]
        (cond
            (pred/level-completed? state)
            (do
                (c/clear-canvas)
                (c/draw-background)
                (state/next-level)
                )
            (pred/game-over? state)
            (do
                (c/clear-canvas)
                (c/draw-background))
            :else
            (if (not @state/paused?); move to predicates
                (let [new-state (update-falling-objects t state)]
                    (c/clear-canvas)
                    (c/draw-background)
                    (draw-player new-state)
                    (draw-falling-objects new-state)
                    (state/set-state new-state)
                    (.requestAnimationFrame js/window draw-frame)
                    )
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
    (if (pred/game-over? (state/get-state))
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
                                          (pred/playing? (state/get-state))
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
    (let [{:keys [lives level score] :as state} (state/get-state)
          _ @state/level-state ; just to make sure ui component re-renders so it knows when level is over
          game-over? (pred/game-over? state)
          game-completed? (pred/game-completed? state)
          level-completed? (pred/level-completed? state)
          playing? (and (not game-over?) (not level-completed?) (:started-game? state))
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

