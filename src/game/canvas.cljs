(ns game.canvas)

(defn- make-canvas
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
(defonce WIDTH (.-width canvas))
(defonce HEIGHT (.-height canvas))
(defonce BACKGROUND (.getElementById js/document "backgroundImage"))
(defn clear-canvas
  []
  (.clearRect cx 0 0 WIDTH HEIGHT)
  )

(defn draw-background
  []
  (.drawImage cx BACKGROUND 0 0 WIDTH HEIGHT)
  )

(defn draw-image
  [img x y w h]
  (.drawImage cx img x y w h)
  )

