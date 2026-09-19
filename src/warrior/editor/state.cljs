(ns warrior.editor.state
  (:require
    [clojure.string :as string]
    [goog.crypt :as crypt]
    [goog.crypt.base64 :as base64]
    [reagent.core :as r]
    [zprint.core :as z]
    [warrior.editor.eval :as eval]
    [warrior.editor.starter :as starter]
    [warrior.ui.game :as game]))

(defonce editor-state
  (r/atom {:code ""
           :error nil}))

(def storage-key "warrior.editor/code")

(def dark-mode-query
  (.matchMedia js/window "(prefers-color-scheme: dark)"))

(defonce dark-mode?
  (r/atom (.-matches dark-mode-query)))

(defonce dark-mode-listener
  (.addEventListener dark-mode-query "change"
                     (fn [event]
                       (reset! dark-mode? (.-matches event)))))

(defn save-code! [code]
  (try
    (.setItem js/localStorage storage-key code)
    (catch :default _
      nil)))

(defn load-code []
  (try
    (.getItem js/localStorage storage-key)
    (catch :default _
      nil)))

(defn encode-code [code]
  (base64/encodeByteArray (crypt/stringToUtf8ByteArray code)
                          base64/Alphabet.WEBSAFE_NO_PADDING))

(defn decode-code [encoded]
  (crypt/utf8ByteArrayToString (base64/decodeStringToByteArray encoded)))

(defn code-from-url []
  (let [hash (subs (.. js/location -hash) 1)]
    (when (not (string/blank? hash))
      (try
        (decode-code hash)
        (catch :default _
          nil)))))

(defn initial-code []
  (or (code-from-url)
      (load-code)
      starter/code))

(defn set-code! [code]
  (swap! editor-state assoc :code code)
  (save-code! code))

(defn init-code! []
  (set-code! (initial-code)))

(defn reset-code! []
  (set-code! starter/code))

(defn format-code [code]
  (try
    (z/zprint-str code {:parse-string-all? true
                        :width 60
                        :map {:comma? false}
                        :style :respect-nl})
    (catch :default _
      code)))

(defn format-code! []
  (set-code! (format-code (:code @editor-state))))

(defn share! []
  (set! (.. js/location -hash) (encode-code (:code @editor-state)))
  (when-let [clipboard (.. js/navigator -clipboard)]
    (.writeText clipboard (.. js/location -href))))

(defn run-code! []
  (try
    (let [play-turn (eval/compile-play-turn (:code @editor-state))]
      (swap! editor-state assoc :error nil)
      (game/run-bot! play-turn))
    (catch :default error
      (swap! editor-state assoc :error (ex-message error)))))
