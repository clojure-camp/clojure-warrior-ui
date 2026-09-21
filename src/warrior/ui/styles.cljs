(ns warrior.ui.styles
  (:require
    [garden.core :as garden]
    [garden.stylesheet :as stylesheet]))

(def monospace "ui-monospace, 'Cascadia Code', 'Fira Code', monospace")

(def toolbar-height "34px")

(def button-styles
  [:button
   {:font-family monospace
    :font-size "13px"
    :line-height 1
    :padding "0.5em 0.9em"
    :color "#2b2b2b"
    :background "#fff"
    :border "1px solid #ccc"
    :border-radius "3px"
    :cursor "pointer"}

   ["&:hover:not(:disabled)"
    {:background "#eee"}]

   ["&:active:not(:disabled)"
    {:background "#ddd"}]

   [:&:disabled
    {:opacity 0.35
     :cursor "default"}]])

(def dark-button-styles
  [:button
   {:color "#abb2bf"
    :background "#3a3f4b"
    :border-color "#181a1f"}

   ["&:hover:not(:disabled)"
    {:background "#454b57"}]

   ["&:active:not(:disabled)"
    {:background "#2f333d"}]])

(defn level-styles []
  [:.level
   {:display "flex"
    :flex-direction "column"
    :font-size "13px"
    :height "100vh"
    :min-width (str (* 11 64) "px")
    :max-width (str (* 11 64) "px")
    :width "max-content"}

   [:.navigator
    {:display "flex"
     :align-items "center"
     :box-sizing "border-box"
     :height toolbar-height
     :padding "0 0.5em"
     :background "#000"
     :color "#fff"}

    button-styles
    dark-button-styles

    [:input
     {:flex-basis "100%"
      :margin "0 0.35em"
      :color-scheme "dark"
      :accent-color "#3d5afe"}]

    [:.play-toggle
     {:margin-left "0.5em"}]]

   [:.status
    {:font-family monospace
     :background "#000"
     :color "#fff"}

    [:.status-bar
     {:display "flex"
      :align-items "center"
      :gap "0.5em"
      :padding "0.25em 0.5em"}

     [:.stats
      {:display "flex"
       :gap "0.35em"
       :padding "0.25em 0.75em"
       :white-space "nowrap"
       :color "#e5e6c7"
       :font-family monospace}

      [".item + .item::before"
       {:content "\"\\00b7\""
        :color "#666"}]

      [:.item
       {:display "flex"
        :gap "0.35em"}

       [:.label
        {:color "#999"}]

       ;; fixed width so the values do not shift as digits change
       [:.value
        {:font-weight "bold"
         :display "inline-block"
         :min-width "3ch"
         :text-align "right"}]

       [:.value.level-number
        {:min-width "1ch"}]

       [:&.total
        [:.value
         {:min-width "4ch"
          :color "#ecc111"}]]]]

     [:.debug-toggle
      {:margin-left "auto"
       :padding "0.25em 0.5em"
       :font-size "0.9em"
       :cursor "pointer"}

      [:&.empty
       {:opacity 0.35}]]]

    [:.debug-columns
     {:display "flex"
      :gap "0.75em"
      :padding "0.5em"
      :height "20em"
      :font-size "0.9em"
      :overflow-y "auto"}

     [:.column
      {:flex "0 0 auto"}

      [:&.say
       {:padding "0.25em 0.5em"
        :margin "-0.25em 0"
        :background "#1f1f1f"
        :border-radius "3px"}]

      [:.label
       {:color "#999"
        :margin-bottom "0.25em"}]

      [:pre
       {:margin "0 0 0.25em"
        :white-space "pre"}]]]]

   [:.messages
    {:overflow-y "auto"
     :overflow-x "auto"
     :font-family monospace
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

     [:&.system]

     [:&.enemy-action
      {:color "#da4939"}]

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
       {:margin-bottom "0.25em"}]

      [:.tip :.clue
       {:color "#666"}

       [:summary
        {:cursor "pointer"}]

       [:.content
        {:margin-top "0.5em"}]]]

     [:&.level-score
      {:margin "0.5em 0.5em"
       :padding "0.75em 1em"
       :border-left "3px solid #ecc111"
       :background "#fff"
       :display "inline-block"}

      [:.title
       {:font-weight "bold"
        :text-transform "uppercase"
        :letter-spacing "0.1em"
        :margin-bottom "0.5em"}]

      [:table
       {:border-collapse "collapse"}

       [:td
        {:padding "0.1em 0.5em 0.1em 0"}]

       [:td.value
        {:text-align "right"
         :padding-left "1.5em"}]

       [:td.grade
        {:padding "0.1em 0 0.1em 0.5em"}]

       [:tr.level-total
        [:td
         {:border-top "1px solid #ccc"
          :font-weight "bold"}]]

       [:tr.total
        [:td
         {:color "#666"}]]]]

     [:&.tower-grade
      {:margin "1em 0.5em 0.5em"
       :padding "0.75em 1em"
       :border-left "3px solid #ecc111"
       :background "#fff"
       :display "inline-block"}

      [:.title
       {:font-weight "bold"
        :text-transform "uppercase"
        :letter-spacing "0.1em"
        :margin-bottom "0.5em"}]

      [:table
       {:border-collapse "collapse"}

       [:td
        {:padding "0.1em 0.5em 0.1em 0"}]

       [:td.grade
        {:text-align "right"
         :padding "0.1em 0 0.1em 1.5em"}]

       [:tr.level-link
        {:cursor "pointer"}

        [:&:hover
         {:background "#eee"}]]

       [:tr.average
        [:td
         {:border-top "1px solid #ccc"
          :font-weight "bold"}]]]]

     [:.grade-badge
      {:display "inline-block"
       :min-width "1.5em"
       :padding "0 0.3em"
       :margin-left "0.25em"
       :border-radius "3px"
       :text-align "center"
       :font-weight "bold"
       :color "#fff"
       :background "#999"}

      [:&.grade-S
       {:background "#ecc111"}]

      [:&.grade-A :&.grade-B
       {:background "#11a811"}]

      [:&.grade-C :&.grade-D
       {:background "#ec8f11"}]

      [:&.grade-F
       {:background "#da4939"}]]]]

   [:.board
    {:display "flex"
     :flex-shrink 0
     :background "#555"
     :height "64px"}

    [:&.escaped
     {:background-image "url(./sprites/escaped_bg.png)"
      :background-repeat "repeat"
      :background-position "center bottom"
      :justify-content "flex-start"
      :align-items "flex-end"}

     [:.space
      {:margin-left "64px"
       :background-image "none"}]]

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

(defn dark-styles []
  (stylesheet/at-media {:prefers-color-scheme "dark"}
    [:body
     {:color-scheme "dark"
      :color "#abb2bf"}

     [:#app
      {:background "#1e2127"}]]

    [:.level
     [:.messages
      [:.turn
       {:border-top-color "#3a3f4b"}

       [:.turn-label
        {:color "#7f848e"}]]

      [:.message
       ["&.active::before"
        {:color "#61afef"}]

       [:&.enemy-action
        {:color "#e06c75"}]

       [:&.say
        {:background "#353b45"}]

       [:&.level-start
        {:border-left-color "#61afef"
         :background "#282c34"}

        [:.tip :.clue
         {:color "#9da5b4"}]]

       [:&.level-score :&.tower-grade
        {:background "#282c34"}

        [:table
         [:tr.level-total :tr.average
          [:td
           {:border-top-color "#3a3f4b"}]]

         [:tr.total
          [:td
           {:color "#9da5b4"}]]

         [:tr.level-link
          [:&:hover
           {:background "#3a3f4b"}]]]]]]]))

(defn styles-view []
  [:style
   {:type "text/css"
    :dangerouslySetInnerHTML
    {:__html (garden/css
               (main-styles)
               (level-styles)
               (dark-styles))}}])
