(ns warrior.editor.styles
  (:require
    [garden.core :as garden]
    [garden.stylesheet :as stylesheet]
    [warrior.ui.styles :as ui-styles]))

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
     :box-sizing "border-box"
     :height ui-styles/toolbar-height
     :padding "0 0.5em"
     :border-bottom "1px solid #ddd"}

    ui-styles/button-styles

    [:.run
     {:font-weight "bold"
      :color "#fff"
      :background "blue"
      :border-color "blue"}

     ["&:hover:not(:disabled)"
      {:background "#3d5afe"
       :border-color "#3d5afe"}]

     ["&:active:not(:disabled)"
      {:background "#2a3eb1"
       :border-color "#2a3eb1"}]]

    [:.hint
     {:margin-left "auto"
      :color "#888"
      :font-family ui-styles/monospace
      :font-size "0.85em"}]]

   [:.error
    {:padding "0.5em 0.75em"
     :background "#fde8e8"
     :color "#a00"
     :font-family ui-styles/monospace
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
      :font-family ui-styles/monospace
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

      ui-styles/dark-button-styles

      [:.run
       {:color "#fff"
        :background "#3d5afe"
        :border-color "#3d5afe"}

       ["&:hover:not(:disabled)"
        {:background "#5b72fe"
         :border-color "#5b72fe"}]

       ["&:active:not(:disabled)"
        {:background "#2a3eb1"
         :border-color "#2a3eb1"}]]

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
