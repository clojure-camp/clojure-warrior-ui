(ns warrior.ui.views
  (:require
    [cljs.reader :as edn]
    [clojure.string :as string]
    [reagent.core :as r]
    [zprint.core :as z]
    [warrior.ui.state :as state]
    [warrior.ui.styles :as styles]))

(defn health-bar-view [entity]
  (when (entity :unit/max-health)
    [:div.health-bar
     {:style {:width (* 2 (entity :unit/max-health))}}
     [:div.health
      {:style {:width (* 2 (entity :unit/health))}
       :class (cond
                (< (entity :unit/health) 5) "low"
                (< (entity :unit/health) 10) "medium"
                :else "high")}]]))

(defn annotate-entity [entity]
  (cond
    (= 0 (:unit/health entity))
    (assoc entity :state :dead)

    (:unit/at-stairs entity)
    (assoc entity :state :walk-stairs)

    (:unit/rescued? entity)
    (assoc entity :state :free
                  :unit/type :unit.type/captive)

    (contains? #{:action/walk :action/attack :action/shoot :action/rest :action/rescue}
               (first (:unit/action entity)))
    (assoc entity :state (first (:unit/action entity)))

    (contains? #{:unit.type/warrior
                 :unit.type/sludge
                 :unit.type/thick-sludge
                 :unit.type/archer
                 :unit.type/wizard
                 :unit.type/captive}
               (:unit/type entity))
    (assoc entity :state :base)

    :else
    entity))

(defn entity-view [entity]
  (let [entity (annotate-entity entity)]
    [:div.sprite
     {:class (str (name (:unit/type entity)) " "
                  (name (or (:state entity) :nil)) " "
                  (when (:unit/enemy? entity) "enemy") " "
                  (when (:unit/direction entity)
                    (name (:unit/direction entity))))
      :style {:background-image
              (str "url(./sprites/"
                   (name (:unit/type entity))
                   (when (:state entity)
                     (str "_" (name (:state entity))))
                   ".png)")}}
     [health-bar-view entity]]))

(defn current-level-id [messages]
  (or (->> messages
           (filter (fn [message]
                     (= :message.type/level-start (:message/type message))))
           last
           :message/level
           :level/id)
      0))

(defn time-bonus-remaining [state]
  (max 0 (- (:state/time-bonus state 0)
            (:state/tick state 0))))

(defn score-badge-view [current-state]
  [:div.score-badge
   [:span.item
    [:span.label "Points"]
    [:span.value (:state/level-points current-state 0)]]
   [:span.item
    [:span.label "Time Bonus"]
    [:span.value (time-bonus-remaining current-state)]]
   [:span.item.total
    [:span.label "Score"]
    [:span.value (:state/score current-state 0)]]])

(defn navigator-view []
  (let [{:keys [index history playing?]} @state/app-state
        state-count (count history)
        current-state (get history index)
        level-id (current-level-id (:state/messages current-state))]
    [:div.navigator
     [:div.level-badge
      "Level " [:span.value.level-number level-id]
      " · Turn " [:span.value.turn-number (:state/turn current-state)]]
     [score-badge-view current-state]
     [:button {:disabled (= index 0)
               :on-click (fn []
                           (swap! state/app-state update :index dec))} "◀"]
     [:input {:type "range"
              :min 0
              :max (dec state-count)
              :step 1
              :value index
              :on-change (fn [e]
                           (swap! state/app-state assoc :index
                                  (js/parseInt (.. e -target -value) 10)))}]
     [:button {:disabled (= index (dec state-count))
               :on-click (fn []
                           (swap! state/app-state update :index inc))} "▶"]
     [:button.play-toggle {:on-click (fn []
                                       (state/toggle-playback!))}
      (if playing?
        "⏸"
        "⏵")]]))

(def zprint-options
  {:style [:community :hiccup]
   :binding {:force-nl? true}
   :set {:sort? true}
   :map {:comma? false
         :lift-ns? false
         :force-nl? true}
   :fn-map {"if" :arg1-force-nl
            "when" :arg1-force-nl
            "fn" :binding
            "rcf/tests" :flow-body}})

(defn format-edn [value]
  (z/zprint-str value 60 zprint-options))

(defn format-say-text [text]
  (try
    (format-edn (edn/read-string text))
    (catch :default _
      (str text))))

(defn message-count-at-index [history index]
  (count (get-in history [index :state/messages])))

(def debug-message-types
  #{:message.type/input
    :message.type/say
    :message.type/warrior-action})

(defn debug-columns-view [messages]
  (let [{inputs :message.type/input
         says :message.type/say
         outputs :message.type/warrior-action} (group-by :message/type messages)]
    [:div.debug-columns
     [:div.column
      [:div.label "input"]
      [:pre (format-edn (:message/board (first inputs)))]]
     (when (seq says)
       [:div.column
        [:div.label "say"]
        (for [[index say] (map-indexed vector says)]
          ^{:key index}
          [:pre (format-say-text (:message/text say))])])
     [:div.column
      [:div.label "output"]
      (when-let [output (first outputs)]
        [:pre (pr-str (:message/action output))])]]))

(defn active-turn-debug-messages [history index]
  (let [all-messages (get-in history [(dec (count history)) :state/messages])
        active-index (dec (message-count-at-index history index))
        active-turn (:message/turn (get all-messages active-index))]
    (filter (fn [message]
              (and
                (= active-turn (:message/turn message))
                (contains? debug-message-types (:message/type message))))
            all-messages)))

(defn debug-view []
  (r/with-let [debug-open? (r/atom false)]
    (let [{:keys [history index]} @state/app-state
          debug-messages (active-turn-debug-messages history index)]
      [:div.debug
       [:div.debug-toggle
        {:class (when (empty? debug-messages) "empty")
         :on-click (fn []
                     (swap! debug-open? not))}
        (if @debug-open?
          "▾ debug"
          "▸ debug")]
       (when (and
               @debug-open?
               (seq debug-messages))
         [debug-columns-view debug-messages])])))

(def message-text-overrides
  {"You have reached the top of the tower"
   "Congratulations, you have escaped!"})

(defn display-text [message]
  (get message-text-overrides
       (:message/text message)
       (:message/text message)))

(defn message-view [message attrs]
  (case (:message/type message)
    :message.type/say
    [:div.message.say attrs
     [:pre (format-say-text (:message/text message))]]

    :message.type/level-start
    (let [level (:message/level message)]
      [:div.message.level-start attrs
       [:div.title "Level " (:level/id level)]
       [:div.description (:level/description level)]
       (when-let [ace-score (:level/ace-score level)]
         [:div.ace-score "Ace score: " ace-score])
       (when-let [tip (:level/tip level)]
         [:details.tip
          [:summary "Show Tip"]
          [:div.content tip]])
       (when-let [clue (:level/clue level)]
         [:details.clue
          [:summary "Show Clue"]
          [:div.content clue]])])

    :message.type/enemy-action
    [:div.message.enemy-action attrs
     (:message/text message)]

    :message.type/error
    [:div.message.error attrs
     (:message/text message)]

    [:div.message.system attrs
     (display-text message)]))

(defn level-score-view [score total-score attrs]
  [:div.message.level-score attrs
   [:div.title "Level Complete"]
   [:table
    [:tbody
     [:tr
      [:td "Points"]
      [:td.value (:score/points score)]]
     [:tr
      [:td "Time Bonus"]
      [:td.value (:score/time-bonus score)]]
     (when (pos? (:score/clear-bonus score))
       [:tr
        [:td "Clear Bonus"]
        [:td.value (:score/clear-bonus score)]])
     [:tr.level-total
      [:td "Level Score"]
      [:td.value (:score/total score)]]
     [:tr.total
      [:td "Total Score"]
      [:td.value total-score]]]]])

(defn index-for-message [history message-index]
  (->> history
       (keep-indexed (fn [index _state]
                       (when (> (message-count-at-index history index) message-index)
                         index)))
       first))

(def score-tally-text-prefixes
  ["Level Score:"
   "Time Bonus:"
   "Clear Bonus:"
   "Total Score:"])

;; the engine emits these as plain text alongside the level-score message;
;; the score card replaces them
(defn score-tally-text-message? [message]
  (and
    (= :message.type/system (:message/type message))
    (some (fn [prefix]
            (string/starts-with? (:message/text message "") prefix))
          score-tally-text-prefixes)))

(defn turn-view [indexed-messages active-index history]
  (let [turn (:message/turn (second (first indexed-messages)))
        log-messages (remove (fn [[_index message]]
                               (or
                                 (contains? debug-message-types (:message/type message))
                                 (score-tally-text-message? message)))
                             indexed-messages)]
    [:div.turn
     [:div.turn-label
      (when (pos? turn)
        turn)]
     [:div.turn-messages
      (for [[index message] log-messages]
        (let [attrs {:class (cond
                              (= index active-index) "active"
                              (> index active-index) "future")
                     :on-click (fn []
                                 (swap! state/app-state assoc :index
                                        (index-for-message history index)))}]
          (with-meta
            (if (= :message.type/level-score (:message/type message))
              [level-score-view
               (:message/score message)
               (get-in history [(index-for-message history index) :state/score])
               attrs]
              [message-view message attrs])
            {:key index})))]]))

(defn scroll-active-message-into-view! [element]
  (when-let [active (some-> element (.querySelector ".message.active"))]
    (.scrollIntoView active #js {:block "nearest"})))

(defn messages-view []
  (let [element (atom nil)]
    (r/create-class
      {:component-did-mount
       (fn []
         (scroll-active-message-into-view! @element))
       :component-did-update
       (fn []
         (scroll-active-message-into-view! @element))
       :reagent-render
       (fn []
         (let [{:keys [history index]} @state/app-state
               all-messages (get-in history [(dec (count history)) :state/messages])
               active-index (dec (message-count-at-index history index))]
           [:div.messages
            {:ref (fn [el]
                    (when el
                      (reset! element el)))}
            (->> all-messages
                 (map-indexed vector)
                 (partition-by (fn [[_index message]]
                                 (:message/turn message)))
                 (map (fn [indexed-messages]
                        (let [turn (:message/turn (second (first indexed-messages)))]
                          ^{:key turn}
                          [turn-view indexed-messages active-index history]))))]))})))

(defn board-view [board]
  (into [:div.board]
        (for [row board]
          (into [:div.row]
                (for [entity (-> row
                                 ;; remove walls at leftmost and rightmost
                                 rest butlast)]
                  [:div.space
                   [entity-view entity]])))))

(defn escaped-view []
  [:div.board.escaped
   [:div.space
    [entity-view {:unit/type :unit.type/warrior}]]])

(defn escaped? [history index]
  (let [current-state (get history index)]
    (and
      (= index (dec (count history)))
      (some? (:state/board current-state))
      (not (:state/game-over? current-state)))))

(defn level-view []
  (let [{:keys [history index]} @state/app-state]
    (if (seq history)
      [:div.level
       [navigator-view]
       (if (escaped? history index)
         [escaped-view]
         [board-view (get-in history [index :state/board])])
       [debug-view]
       [messages-view]]
      [:div.level])))

(defn app-view []
  [:div.app
   [styles/styles-view]
   [level-view]])
