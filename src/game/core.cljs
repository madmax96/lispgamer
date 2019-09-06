(ns game.core
    (:require [cljs.core.async
               :as a])
    )

(enable-console-print!)

(defn make-canvas
    [parent-element]
    (let [canvas (.createElement js/document "canvas")]
        (set! (.-width canvas) (.-clientWidth parent-element))
        (set! (.-height canvas) (.-clientHeight parent-element))
        (.appendChild parent-element canvas)
        canvas
        )
    )

(defonce canvas (make-canvas (.getElementById js/document "gameContainer")))
(defonce cx (.getContext canvas "2d"))
(defonce CANVAS-WIDTH (.-width canvas))
(defonce CANVAS-HEIGHT (.-height canvas))
(defonce PLAYER-WIDTH (.round js/Math (* (/ 15 100) CANVAS-WIDTH)))
(defonce PLAYER-HEIGHT (.round js/Math (* (/ 8 100) CANVAS-HEIGHT)))
(defonce OBJECT-SIZE (.round js/Math (* (/ 3 100) CANVAS-WIDTH)))

(defonce MENU (.getElementById js/document "menu"))
(defonce PLAY-BUTTON (.getElementById js/document "play"))
(defonce player (atom {:x 0 :y (- CANVAS-HEIGHT PLAYER-HEIGHT) :img (.getElementById js/document "playerImage")}))
(defonce background (.getElementById js/document "backgroundImage"))

(defonce objects {
                  :lambda {
                           :speed-factor 1
                           :img (.getElementById js/document "lambdaImage")
                           }
                  :bug {
                        :speed-factor 2
                        :img (.getElementById js/document "bugImage")
                        }
                  })

(defonce levels-config [{}])
(defonce current-level (atom 1))
(defonce falling-objects (atom '()))
(defonce last-timestamp (atom nil))

(defn clear-canvas
    []
    (.clearRect cx 0 0 CANVAS-WIDTH CANVAS-HEIGHT)
    )

(defn draw-background
    []
    (.drawImage cx background 0 0 CANVAS-WIDTH CANVAS-HEIGHT)
    )

(defn draw-player
    []
    (let [{x :x y :y img :img} @player]
        (.drawImage cx img x y PLAYER-WIDTH PLAYER-HEIGHT)
        )
    )

(defn update-falling-objects
    [t]
    (if
        (or
            (not @last-timestamp)
            (> (- t @last-timestamp) (+ 500 (rand-int 4500))))

        (do
            (swap! falling-objects conj {
                                         :x (rand (- CANVAS-WIDTH OBJECT-SIZE))
                                         :y 0
                                         :img (:img (:lambda objects))
                                         :speed (+ 5 (rand 5))
                                         })
            (reset! last-timestamp t)
            )
        )
    (reset! falling-objects
            (map
                (fn [obj] (conj obj {:y (+ (:y obj) (:speed obj))}))
                @falling-objects
                )
            )
    )

(defn draw-falling-objects
    [t]
    (update-falling-objects t)
    (doseq [obj @falling-objects]
        (.drawImage cx (:img obj) (:x obj) (:y obj) OBJECT-SIZE OBJECT-SIZE)
        )
    )

(defn play-sound
    "plays sound that corresponds to an event"
    [event]
    )

(defn update-element
    [content]
    (let [el (.getElementById js/document "app")]
        (set! (.-innerHTML el) content)
        )
    )

;predicates

(defn game-over?
    []
    )

(defn level-completed?
    []
    )

(defn object-lost?
    [player object]
    )

(defn object-caught?
    [player object]

    )

(defn draw-frame
    "draws single frame"
    [timestamp]
    (clear-canvas)
    (draw-background)
    (draw-player)
    (draw-falling-objects timestamp)
    ; check for collisions and update score,lives and objects

    (.requestAnimationFrame js/window draw-frame)
    )

(defn hide-menu
    []
    (set! (.-display (.-style MENU)) "none")
    )

(defn calculate-user-x
    [offset-x]
    (.min js/Math (.max js/Math 0 (- offset-x (/ PLAYER-WIDTH 2) )) (- CANVAS-WIDTH PLAYER-WIDTH) )
    )
(defn start-game
    []
    (hide-menu)
    (set! (.-onmousemove canvas) #(swap! player conj {:x (calculate-user-x (.-offsetX %))}) )
    (.requestAnimationFrame js/window draw-frame)
    )

(defn init-game
    []
    (draw-background)
    (set! (.-onclick PLAY-BUTTON) start-game )
    )

(init-game)

;(update-element "<p>test</p>")
;(.appendChild (.getElementById js/document "app") (create-img))