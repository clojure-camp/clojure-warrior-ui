(ns warrior.bot
  (:require
    [clojure-warrior.api :as w]))

;; Save this file and the browser re-runs your bot against all levels.

(defn play-turn [board]
  (w/say {:health (:unit/health (w/warrior board))})
  ;; What should we do?
  [:TODO])


;; Helper functions
;;
;; (w/warrior board)
;; (w/feel board :direction/forward)
;; (w/look board :direction/forward)
;; (w/listen board)
;; (w/stairs board)
;; (w/inspect board [4 0])
;; (w/distance-to board [4 0])
;; (w/say {:health 20.0})
;;
;; More details in docstrings and the README.
