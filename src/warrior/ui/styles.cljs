(ns warrior.ui.styles
  (:require
    [garden.core :as garden]))

(defn level-styles []
  [:.level
   {:display "flex"
    :flex-direction "column"
    :height "100vh"
    :min-width (str (* 11 64) "px")
    :max-width (str (* 11 64) "px")
    :width "max-content"}

   [:.navigator
    {:display "flex"
     :align-items "center"
     :min-height "2em"}

    [:.level-badge
     {:padding "0.25em 0.75em"
      :font-weight "bold"
      :white-space "nowrap"
      :color "#e5e6c7"
      :background "blue"
      :border-radius "3px"
      :margin-right "0.5em"
      :font-family "monospace"}]

    [:input
     {:flex-basis "100%"}]

    [:.play-toggle
     {:margin-left "0.5em"}]]

   [:.debug
    {:font-family "monospace"
     :padding "0 0.5em"
     :background "#000"
     :color "#fff"
     :font-size "0.8em"}

    [:.debug-toggle
     {
      :padding "0.25em 0.5em"
      :cursor "pointer"}

     [:&.empty
      {:opacity 0.35}]]

    [:.debug-columns
     {:display "flex"
      :gap "1em"
      :margin "0.25em 0 0.5em"
      :padding "0.5em"
      :border-radius "3px"
      :max-height "40vh"
      :overflow-y "auto"}

     [:.column
      {:flex "0 0 auto"}

      [:.label
       {:color "#999"
        :margin-bottom "0.25em"}]

      [:pre
       {:margin "0 0 0.25em"
        :white-space "pre"}]]]]

   [:.messages
    {:overflow-y "auto"
     :overflow-x "auto"
     :font-family "monospace"
     :flex-basis "50%"
     :flex-grow 2
     :padding "0.5em"}

    [:.turn
     {:display "flex"
      :margin-top "0.5em"
      :padding-top "0.5em"
      :border-top "1px solid #ccc"}

     [:.turn-label
      {:flex "0 0 3em"
       :line-height "1.25em"
       :padding "0 0.5em"
       :text-align "right"
       :color "#999"}]

     [:.turn-messages
      {:flex "1 1 auto"
       :min-width 0}]]

    [:.message
     {:position "relative"
      :line-height "1.25em"
      :padding "0 0.5em"
      :cursor "pointer"}

     ["&.active::before"
      {:content "\"\\25C6\""
       :position "absolute"
       :left "-0.3em"
       :top 0
       :font-size "0.6em"
       :line-height "2.1em"
       :color "blue"}]

     [:&.future
      {:opacity 0.35}]

     [:&.system
      {:white-space "nowrap"}]

     [:&.enemy-action
      {:white-space "nowrap"
       :color "#da4939"}]

     [:&.error
      {:white-space "pre-wrap"
       :color "white"
       :padding "0.5em"
       :margin "-0.5em 0"
       :background "#da4939"
       :font-weight "bold"}]

     [:&.say
      {:padding "0.75em 1em"
       :border-radius "3px"
       :margin "0.5em 0.5em"
       :display "inline-block"
       :background "#2b2b2b"}

      [:pre
       {:margin 0
        :color "#e5e6c7"
        :white-space "pre"}]]

     [:&.level-start
      {:margin "1em 0.5em 0.5em"
       :padding "0.75em 1em"
       :border-left "3px solid blue"
       :background "#fff"
       :white-space "normal"}

      [:.title
       {:font-weight "bold"
        :text-transform "uppercase"
        :letter-spacing "0.1em"
        :margin-bottom "0.5em"}]

      [:.description
       {:margin-bottom "0.5em"}]

      [:.tip
       {:color "#666"
        :margin-bottom "0.25em"}

       [:.label
        {:font-weight "bold"}]]

      [:.clue
       {:color "#666"}

       [:summary
        {:cursor "pointer"}]]]]]

   [:.board
    {:display "flex"
     :background "#555"
     :height "64px"}

    [:.space
     {:display "inline-block"
      :width "64px"
      :height "64px"
      :background-image "url(./sprites/floor.png)"
      :position "relative"}

     [:.sprite
      {:width "64px"
       :height "64px"
       :background-repeat "no-repeat"
       :background-position-x "center"
       :position "absolute"
       :z-index 100}

      [:&.floor
       {:display "none"}]

      [:&.attack.sludge
       {:left "0px"}]

      [:&.warrior.west :&.enemy.east
       {:transform "scale(-1, 1)"}

       [:.health-bar
        {:transform "scale(-1, 1)"}]]

      [:&.attack.warrior::after
       {:content "\"\""
        :display "block"
        :width "64px"
        :height "64px"
        :background "url(./sprites/warrior_attack-receive.png)"
        :position "absolute"
        :top 0
        :left "64px"
        :z-index 102}]

      [:&.warrior.walk-stairs
       {:background-image "url(./sprites/stairs.png) !important"}]

      [:&.warrior.walk-stairs::after
       {:content "\"\""
        :display "block"
        :width "64px"
        :height "64px"
        :background "url(./sprites/warrior_walk-stairs.png)"
        :position "absolute"
        :top 0
        :left 0
        :z-index 100}]]

     [:.health-bar
      {:height "2px"
       :background "#e5e6c7"
       :margin "4px auto 0"}

      [:.health
       {:height "100%"}

       [:&.high
        {:background "#11ec11"}]

       [:&.medium
        {:background "#ec8f11"}]

       [:&.low
        {:background "#ec1111"}]]]]]])

(defn main-styles []
  [:body
   {:margin 0
    :padding 0}

   [:#app
    {:background "#eee"}

    [:.app
     {:display "flex"
      :height "100vh"}]]])

(defn styles-view []
  [:style
   {:type "text/css"
    :dangerouslySetInnerHTML
    {:__html (garden/css
               (main-styles)
               (level-styles))}}])
