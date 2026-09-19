(ns warrior.editor.core
  (:require
    [reagent.dom.client :as rdom]
    [warrior.editor.state :as state]
    [warrior.editor.views :as views]))

(defonce root
  (rdom/create-root (.. js/document (getElementById "app"))))

(defn render! []
  (rdom/render root [views/app-view]))

(defn init! []
  (state/init-code!)
  (render!)
  (state/run-code!))

(defn reload! []
  (render!))
