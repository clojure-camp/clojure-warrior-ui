(ns warrior.bot
  (:require
    [clojure-warrior.api :as w]))

;; Save this file and the browser re-runs your bot against all levels.
;; See README.md for the list of actions and board read functions.

(defn play-turn [board]
  (w/say {:health (:unit/health (w/warrior board))})
  ;; What should we do?
  [])
