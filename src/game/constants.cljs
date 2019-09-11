(ns game.constants
  (:require [game.canvas :as c])
  )

(defonce OBJECT-SIZE (.round js/Math (* (/ 5 100) c/WIDTH)))
(defonce PLAYER-WIDTH (.round js/Math (* (/ 25 100) c/WIDTH)))
(defonce PLAYER-MIN-WIDTH (.round js/Math (* (/ 10 100) c/WIDTH)))
(defonce PLAYER-MAX-WIDTH (.round js/Math (* (/ 60 100) c/WIDTH)))
(defonce PLAYER-HEIGHT (.round js/Math (* (/ 8 100) c/HEIGHT)))
(defonce INITIAL-LIVES 3)
(defonce GOOD-CATCH-SCORE-INCREASE 20)
(defonce BAD-CATCH-SCORE-INCREASE -10)
(defonce PLAYER-WIDTH-CHANGE  (.round js/Math (* (/ 2 100) c/WIDTH)))

;Assets
;===========================================================================

;Images
(defonce LAMBDA-OBJECT-IMG (.getElementById js/document "lambdaImage"))
(defonce BUG-OBJECT-IMG (.getElementById js/document "bugImage"))
(defonce ROCK-OBJECT-IMG (.getElementById js/document "rockImage"))
(defonce PLAYER-IMG (.getElementById js/document "playerImage"))
;===========================================================================

;Sounds
(defonce GOOD-CATCH-SOUND (.getElementById js/document "goodCatchSound"))
(defonce BAD-CATCH-SOUND (.getElementById js/document "badCatchSound"))
(defonce BAD-DROP-SOUND (.getElementById js/document "badDropSound"))
(defonce OBJECT-CREATED-SOUND (.getElementById js/document "objectCreatedSound"))
(defonce LEVEL-COMPLETED-SOUND (.getElementById js/document "levelCompletedSound"))
(defonce GAME-OVER-SOUND (.getElementById js/document "gameOverSound"))
