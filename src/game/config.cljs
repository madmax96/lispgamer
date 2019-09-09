(ns game.config
  (:require [game.canvas :as c])
  )

;CONSTANTS
(defonce OBJECT-SIZE (.round js/Math (* (/ 5 100) c/WIDTH)))
(defonce PLAYER-WIDTH (.round js/Math (* (/ 25 100) c/WIDTH)))
(defonce PLAYER-HEIGHT (.round js/Math (* (/ 8 100) c/HEIGHT)))
(defonce INITIAL-LIVES 30)

(defonce falling-objects-config {
                                 :lambda {
                                          :speed-range [1500 2500]
                                          :img (.getElementById js/document "lambdaImage")
                                          :good? true
                                          }
                                 :bug {
                                       :speed-range [1200 1800]
                                       :img (.getElementById js/document "bugImage")
                                       :good? false
                                       }
                                 :rock {
                                       :speed-range [600 1000]
                                       :img (.getElementById js/document "rockImage")
                                        :good? false
                                       }
                                 })

(defonce levels-config [
                        {
                         :objects {:bug 500 :lambda 500}
                         :speed-factor 0
                         :object-gen-interval [150 200]
                         }
                        {
                         :objects {:bug 10 :lambda 15 :rock 5}
                         :speed-factor 100
                         :object-gen-interval [1200 1800]
                         }
                        {
                         :objects {:bug 25 :lambda 20 :rock 20}
                         :speed-factor 200
                         :object-gen-interval [600 1600]
                         }
                        {
                         :objects {:bug 25 :lambda 25 :rock 25}
                         :speed-factor 300
                         :object-gen-interval [400 1000]
                         }
                        ])

(defonce num-of-levels (count levels-config))

(defn get-level-config
  [level]
  (nth levels-config (- level 1))
  )