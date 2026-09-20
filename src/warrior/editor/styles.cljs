(ns warrior.editor.styles
  (:require
    [garden.core :as garden]
    [garden.stylesheet :as stylesheet]
    [warrior.ui.styles :as ui-styles]))

(def monospace "ui-monospace, 'Cascadia Code', 'Fira Code', monospace")

(defn editor-styles []
  [:.editor-panel
   {:display "flex"
    :flex-direction "column"
    :flex 1
    :min-width "360px"
    :height "100vh"
    :background "white"
    :border-right "1px solid #ccc"}

   [:.toolbar
    {:display "flex"
     :gap "0.5em"
     :align-items "center"
     :padding "0.5em"
     :border-bottom "1px solid #ddd"}

    [:button
     {:padding "0.4em 0.9em"
      :cursor "pointer"}]

    [:.run
     {:font-weight "bold"}]

    [:.hint
     {:margin-left "auto"
      :color "#888"
      :font-size "0.85em"}]]

   [:.error
    {:padding "0.5em 0.75em"
     :background "#fde8e8"
     :color "#a00"
     :font-family monospace
     :font-size "0.85em"
     :white-space "pre-wrap"}]

   [:.code-editor
    {:flex 1
     :min-height 0
     :overflow "auto"}

    [:.cm-editor
     {:height "100%"}]

    [:.cm-editor.cm-focused
     {:outline "none"}]

    [:.cm-content
     {:padding "8px"
      :font-family monospace
      :font-size "13px"
      :line-height "1.6"}]]])

(defn editor-dark-styles []
  (stylesheet/at-media {:prefers-color-scheme "dark"}
    [:.editor-panel
     {:background "#282c34"
      :color "#abb2bf"
      :border-right-color "#181a1f"}

     [:.toolbar
      {:border-bottom-color "#181a1f"}

      [:button
       {:background "#3a3f4b"
        :color "#abb2bf"
        :border "1px solid #181a1f"
        :border-radius "3px"}]

      [:.hint
       {:color "#7f848e"}]]

     [:.error
      {:background "#3b2424"
       :color "#e06c75"}]]))

(defn styles-view []
  [:style
   {:type "text/css"
    :dangerouslySetInnerHTML
    {:__html (garden/css
               (ui-styles/main-styles)
               (ui-styles/level-styles)
               (ui-styles/dark-styles)
               (editor-styles)
               (editor-dark-styles))}}])
