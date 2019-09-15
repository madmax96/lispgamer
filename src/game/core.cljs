(ns game.core
  (:require [game.canvas :as c]
            [game.sound :as s]
            [game.config :as config]
            [game.state :as state]
            [reagent.core :as r]
            [game.predicates :as pred]
            [game.constants :as C]))

(enable-console-print!)

(defn update-falling-objects
  [t {:keys [last-timestamp falling-objects level-state] :as state}]
  (let [time-difference (- t last-timestamp)
        {at-boundary true safe false} (group-by #(pred/object-at-boundary? % state) falling-objects)
        {dropped true caught false} (group-by #(pred/object-lost? % state) at-boundary)
        moved-falling-objects  (map
                                (fn [obj]
                                  (config/moveObject obj time-difference))
                                safe)
        s1 (reduce
            (fn [curr-state dropped-obj]
              (config/whenObjectDrop dropped-obj curr-state))
            state
            dropped)
        s2 (reduce
            (fn [curr-state caught-obj]
              (config/whenObjectCaught caught-obj curr-state))
            s1
            caught)]

    (let [s3 (conj s2 {:last-timestamp t
                       :falling-objects moved-falling-objects})]
      (if (pred/create-object? t state)
        (let [new-object (config/create-object level-state)
              object-type (type new-object)
              level-objects (:objects level-state)
              falling-objects  (conj moved-falling-objects new-object)
              s4 (conj s3 {:last-object-created-timestamp t
                           :falling-objects falling-objects})]

          {:new-state (if (= 1 (get level-objects object-type))
                        (update-in s4 [:level-state :objects] dissoc object-type)
                        (update-in s4 [:level-state :objects] update object-type dec))
           :dropped-objects dropped
           :caught-objects caught
           :created-object new-object})

        {:new-state s3
         :dropped-objects dropped
         :caught-objects caught}))))

;Drawing


(defn draw-player
  [{x :x y :y w :w h :h img :img}]
  (c/draw-image img x y w h)
  nil)

(defn draw-falling-objects
  [{falling-objects :falling-objects}]
  (doseq [{:keys [x y] :as O} falling-objects]
    (c/draw-image (config/get-object-image (type O)) x y C/OBJECT-SIZE C/OBJECT-SIZE))
  nil)

(defn draw-frame
  "draws single frame"
  [t]
  (let [state (state/get-state)]
    (cond
      (pred/level-completed? state)
      (do
        (c/draw-background)
        (s/level-completed)
        (reset! state/started-game? false))
      (pred/game-over? state)
      (do
        (c/draw-background)
        (s/game-over)
        (reset! state/started-game? false))
      (not @state/paused?)
      (let [{:keys [new-state dropped-objects caught-objects created-object]} (update-falling-objects t state)]
                ;play sounds
        (doseq [o dropped-objects]
          (s/object-lost o))
        (doseq [o caught-objects]
          (s/object-caught o))
        (when created-object (s/object-created))
        (c/draw-background)
        (draw-player (:player new-state))
        (draw-falling-objects new-state)
        (state/set-state new-state)
        (.requestAnimationFrame js/window draw-frame)))))

;Player Event Handlers


(defn calculate-player-x
  [offset-x player]
  (let [{w :w} player]
    (.min js/Math (.max js/Math 0 (- offset-x (/ w 2))) (- c/WIDTH w))))

(defn handle-mousemove
  [event]
  (state/move-player (calculate-player-x (.-offsetX event) (:player (state/get-state)))))

(defn handle-keydown
  [event]
  (when (and
         (pred/playing? (state/get-state))
         (= (.-keyCode event) 32))
    (do
      (state/toggle-pause)
      (when (not @state/paused?) (draw-frame @state/last-timestamp)))))

(defn start-level
  []
  (if (pred/game-over? (state/get-state))
    (state/reset-game-state))
  (set! (.-onmousemove c/canvas) handle-mousemove)
  (reset! state/started-game? true)
  (state/reset-level-state)
  (.requestAnimationFrame js/window draw-frame))

(set! (.-onkeydown js/document) handle-keydown)
;UI
(defn game-ui []
  (let [{:keys [lives current-level score] :as state} (state/get-state)
        game-over? (pred/game-over? state)
        game-completed? (pred/game-completed? state)
        level-completed? (pred/level-completed? state)
        playing? (pred/playing? state)
        menu (cond
               playing? nil
               game-over? [:div.menu
                           [:p.game-over "Game over"]
                           [:button {:on-click #(state/reset-game-state)} "Try Again"]]
               game-completed? [:div.menu
                                [:p.game-completed "Congratulations!!! Game Completed"]
                                [:button {:on-click #(state/reset-game-state)} "Play Again"]]
               level-completed? [:div.menu
                                 [:p.level-completed "Level Completed"]
                                 [:button {:on-click #(do (state/next-level) (start-level))} "Next Level"]]
               :else [:div.menu
                      [:button {:on-click start-level} "Play"]])]

    [:div.ui-container
     [:div.game-info
      [:p "Lives: " lives]
      [:p "Score: " score]
      [:p "Level: " current-level]]
     menu]))

(c/draw-background)
(state/reset-game-state)
(r/render [game-ui] (.getElementById js/document "ui"))

