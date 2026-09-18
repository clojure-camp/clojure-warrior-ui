(ns warrior.ui.state
  (:require
    [reagent.core :as r]))

(defonce app-state
  (r/atom {:history []
           :index 0}))
