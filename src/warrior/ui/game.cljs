(ns warrior.ui.game
  (:require
    [clojure.string :as string]
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

(def todo-error-prefix
  (str "Invalid action " (pr-str [:TODO])))

(defn with-todo-hint [message]
  (if (and (= :message.type/error (:message/type message))
           (string/starts-with? (str (:message/text message)) todo-error-prefix))
    (assoc message :message/text "Edit play-turn to return a valid action.")
    message))

(defn with-todo-hints [history]
  (mapv (fn [state]
          (update state :state/messages (fn [messages]
                                          (mapv with-todo-hint messages))))
        history))

(defn run-bot! [play-turn]
  (let [history (try
                  (->> (play/play-levels levels/levels play-turn {:check-abilities? false})
                       without-opening-state
                       with-todo-hints)
                  (catch :default error
                    (js/console.error error)
                    (error-history error)))]
    (swap! state/app-state assoc
           :history history
           :index 0)
    (state/start-playback!)))
