(ns warrior.bot
  (:require
   [clojure-warrior.api :as w]
   [warrior.bot-complete :as bc]))

(defn starter-bot [board]
  (w/say "health:" (:unit/health (w/warrior board)))
  (if (:unit/enemy? (w/feel board :direction/forward))
    [:action/attack :direction/forward]
    [:action/walk :direction/forward]))

(defn play-turn [board]
  (bc/play-turn board))
