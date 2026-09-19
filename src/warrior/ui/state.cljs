(ns warrior.ui.state
  (:require
    [reagent.core :as r]))

(defonce app-state
  (r/atom {:history []
           :index 0
           :playing? false}))

(defonce playback-interval-id
  (atom nil))

(def playback-step-ms 200)

(defn stop-playback! []
  (when-let [interval-id @playback-interval-id]
    (js/clearInterval interval-id)
    (reset! playback-interval-id nil))
  (swap! app-state assoc :playing? false))

(defn step-forward! []
  (let [{:keys [index history]} @app-state]
    (if (< index (dec (count history)))
      (swap! app-state update :index inc)
      (stop-playback!))))

(defn start-playback! []
  (stop-playback!)
  (let [{:keys [index history]} @app-state]
    (when (= index (dec (count history)))
      (swap! app-state assoc :index 0)))
  (reset! playback-interval-id (js/setInterval step-forward! playback-step-ms))
  (swap! app-state assoc :playing? true))

(defn toggle-playback! []
  (if (:playing? @app-state)
    (stop-playback!)
    (start-playback!)))
