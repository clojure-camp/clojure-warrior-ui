(ns warrior.ui.game
  (:require
    [clojure-warrior.levels :as levels]
    [clojure-warrior.play :as play]
    [warrior.ui.state :as state]))

(defn error-history [error]
  [{:state/turn 0
    :state/messages [{:message/type :message.type/error
                      :message/text (str "Error while running your bot: "
                                         (or (ex-message error)
                                             (str error)))
                      :message/turn 0}]}])

;; the engine's first state ("You enter the tower") has no board,
;; and its message is carried into every later state
(defn without-opening-state [history]
  (->> history
       rest
       (mapv (fn [state]
               (update state :state/messages (fn [messages]
                                               (vec (rest messages))))))))

(defn run-bot! [play-turn]
  (let [history (try
                  (without-opening-state (play/play-levels levels/levels play-turn))
                  (catch :default error
                    (js/console.error error)
                    (error-history error)))]
    (swap! state/app-state assoc
           :history history
           :index 0)
    (state/start-playback!)))
