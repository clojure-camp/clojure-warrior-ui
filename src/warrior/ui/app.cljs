(ns warrior.ui.app
  (:require
    [clojure-warrior.levels :as levels]
    [clojure-warrior.play :as play]
    [reagent.dom.client :as rdom]
    [warrior.bot :as bot]
    [warrior.ui.state :as state]
    [warrior.ui.views :as views]))

(defn error-history [error]
  [{:state/messages [{:message/type :message.type/error
                      :message/text (str "Error while running your bot: "
                                         (or (ex-message error)
                                             (str error)))}]}])

(defn run-bot! []
  (let [history (try
                  (vec (play/play-levels levels/levels bot/play-turn))
                  (catch :default error
                    (js/console.error error)
                    (error-history error)))]
    (swap! state/app-state assoc
           :history history
           :turn (dec (count history)))))

(defonce root
  (rdom/create-root (.. js/document (getElementById "app"))))

(defn render! []
  (rdom/render root [views/app-view]))

(defn init! []
  (run-bot!)
  (render!))

(defn reload! []
  (run-bot!)
  (render!))
