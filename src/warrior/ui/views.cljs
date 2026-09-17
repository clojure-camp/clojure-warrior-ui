(ns warrior.ui.views
  (:require
    [cljs.reader :as edn]
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

(defn navigator-view []
  (let [{:keys [turn history]} @state/app-state
        turn-count (count history)
        level-id (current-level-id (get-in history [turn :state/messages]))]
    [:div.navigator
     [:div.level-badge "Level " level-id]
     [:button {:disabled (= turn 0)
               :on-click (fn []
                           (swap! state/app-state update :turn dec))} "◀"]
     [:input {:type "range"
              :min 0
              :max (dec turn-count)
              :step 1
              :value turn
              :on-change (fn [e]
                           (swap! state/app-state assoc :turn
                                  (js/parseInt (.. e -target -value) 10)))}]
     [:button {:disabled (= turn (dec turn-count))
               :on-click (fn []
                           (swap! state/app-state update :turn inc))} "▶"]]))

(defn format-say-text [text]
  (try
    (z/zprint-str (edn/read-string text)
                  60
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
    (catch :default _
      (str text))))

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
       (when-let [tip (:level/tip level)]
         [:div.tip
          [:span.label "Tip: "]
          tip])
       (when-let [clue (:level/clue level)]
         [:details.clue
          [:summary "Show clue"]
          clue])])

    :message.type/enemy-action
    [:div.message.enemy-action attrs
     (:message/text message)]

    [:div.message.system attrs
     (:message/text message)]))

(defn message-count-at-turn [history turn]
  (count (get-in history [turn :state/messages])))

(defn turn-for-message [history message-index]
  (->> history
       (keep-indexed (fn [turn _state]
                       (when (> (message-count-at-turn history turn) message-index)
                         turn)))
       first))

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
         (let [{:keys [history turn]} @state/app-state
               all-messages (get-in history [(dec (count history)) :state/messages])
               active-index (dec (message-count-at-turn history turn))]
           [:div.messages
            {:ref (fn [el]
                    (when el
                      (reset! element el)))}
            (map-indexed
              (fn [index message]
                ^{:key index}
                [message-view message
                 {:class (cond
                           (= index active-index) "active"
                           (> index active-index) "future")
                  :on-click (fn []
                              (swap! state/app-state assoc :turn
                                     (turn-for-message history index)))}])
              all-messages)]))})))

(defn board-view [board]
  (into [:div.board]
        (for [row board]
          (into [:div.row]
                (for [entity (-> row
                                 ;; remove walls at leftmost and rightmost
                                 rest butlast)]
                  [:div.space
                   [entity-view entity]])))))

(defn error-view []
  (when-let [error (@state/app-state :error)]
    [:div.error error]))

(defn level-view []
  (let [{:keys [history turn]} @state/app-state]
    (if (seq history)
      [:div.level
       [navigator-view]
       [board-view (get-in history [turn :state/board])]
       [messages-view]]
      [:div.level])))

(defn app-view []
  [:div.app
   [styles/styles-view]
   [error-view]
   [level-view]])
