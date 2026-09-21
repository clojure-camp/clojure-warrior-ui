(ns warrior.editor.views
  (:require
    ["@codemirror/commands" :refer [defaultKeymap history historyKeymap]]
    ["@codemirror/language" :refer [StreamLanguage defaultHighlightStyle syntaxHighlighting]]
    ["@codemirror/legacy-modes/mode/clojure" :as cm-clojure]
    ["@codemirror/state" :refer [Compartment EditorState Prec]]
    ["@codemirror/theme-one-dark" :refer [oneDark]]
    ["@codemirror/view" :refer [EditorView keymap]]
    ["@jurjanpaul/codemirror6-parinfer" :refer [parinferExtension]]
    [reagent.core :as r]
    [warrior.editor.state :as editor-state]
    [warrior.editor.styles :as styles]
    [warrior.ui.views :as ui-views]))

(def theme-compartment (Compartment.))

(defn theme-extension [dark?]
  (if dark?
    oneDark
    (syntaxHighlighting defaultHighlightStyle)))

(defn editor-extensions [{:keys [dark? on-change on-run on-format]}]
  #js [(history)
       (parinferExtension)
       (.of theme-compartment (theme-extension dark?))
       (.define StreamLanguage (.-clojure cm-clojure))
       (.of keymap (.concat defaultKeymap historyKeymap))
       (.highest Prec
                 (.of keymap #js [#js {:key "Mod-Enter"
                                       :run (fn []
                                              (on-run)
                                              true)}
                                  #js {:key "Tab"
                                       :run (fn []
                                              (on-format)
                                              true)}]))
       (.-lineWrapping EditorView)
       (.of (.-updateListener EditorView)
            (fn [^js update]
              (when (.-docChanged update)
                (on-change (.. update -state -doc toString)))))])

(defn create-editor-view [{:keys [value parent] :as options}]
  (EditorView. #js {:state (.create EditorState
                                    #js {:doc value
                                         :extensions (editor-extensions options)})
                    :parent parent}))

(defn editor-text [^js view]
  (.. view -state -doc toString))

(defn replace-editor-text! [^js view text]
  (.dispatch view #js {:changes #js {:from 0
                                     :to (.. view -state -doc -length)
                                     :insert text}}))

(defn set-editor-theme! [^js view dark?]
  (.dispatch view #js {:effects (.reconfigure theme-compartment (theme-extension dark?))}))

(defn code-editor [_options]
  (let [view-atom (atom nil)
        container-atom (atom nil)]
    (r/create-class
      {:display-name "code-editor"

       :component-did-mount
       (fn [this]
         (reset! view-atom
                 (create-editor-view (assoc (r/props this)
                                            :parent @container-atom))))

       :component-did-update
       (fn [this old-argv]
         (let [{:keys [value dark?]} (r/props this)
               old-dark? (:dark? (second old-argv))
               view @view-atom]
           (when (not= value (editor-text view))
             (replace-editor-text! view value))
           (when (not= dark? old-dark?)
             (set-editor-theme! view dark?))))

       :component-will-unmount
       (fn [_]
         (some-> ^js @view-atom .destroy))

       :reagent-render
       (fn [_]
         [:div.code-editor
          {:ref (fn [element]
                  (reset! container-atom element))}])})))

(defn editor-panel-view []
  (let [{:keys [code error]} @editor-state/editor-state
        dark? @editor-state/dark-mode?]
    [:div.editor-panel
     [:div.toolbar
      [:button.run {:on-click (fn []
                                (editor-state/run-code!))}
       "▶ Run"]
      [:button {:on-click (fn []
                            (editor-state/format-code!))}
       "Format"]
      [:button {:on-click (fn []
                            (editor-state/reset-code!))}
       "Reset"]
      [:button {:on-click (fn []
                            (editor-state/share!))}
       "Share"]
      [:span.hint "Cmd/Ctrl-Enter to run · Tab to format"]
      [:a.help {:href "https://github.com/clojure-camp/clojure-warrior-ui#how-to-play"
                :target "_blank"
                :rel "noopener noreferrer"}
       "How to Play"]]
     (when error
       [:div.error error])
     [code-editor {:value code
                   :dark? dark?
                   :on-change editor-state/set-code!
                   :on-run editor-state/run-code!
                   :on-format editor-state/format-code!}]]))

(defn app-view []
  [:div.app
   [styles/styles-view]
   [editor-panel-view]
   [ui-views/level-view]])
