(ns warrior.ui.app
  (:require
    [clojure-warrior.levels :as levels]
    [clojure-warrior.play :as play]
    [reagent.dom.client :as rdom]
    [warrior.bot :as bot]
    [warrior.ui.state :as state]
    [warrior.ui.views :as views]))

(defn run-bot! []
  (try
    (let [history (vec (play/play-levels levels/levels bot/play-turn))]
      (swap! state/app-state assoc
             :history history
             :turn (dec (count history))
             :error nil))
    (catch :default error
      (js/console.error error)
      (swap! state/app-state assoc :error (str error)))))

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
