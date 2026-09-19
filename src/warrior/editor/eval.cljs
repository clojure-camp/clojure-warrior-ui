(ns warrior.editor.eval
  (:require
    [clojure-warrior.api]
    [sci.core :as sci]))

(def sci-ctx
  (sci/init {:namespaces {'clojure-warrior.api
                          (sci/copy-ns clojure-warrior.api
                                       (sci/create-ns 'clojure-warrior.api)
                                       {:exclude ['*say-listener*]})}}))

(defn error-message [error]
  (let [{:keys [line column]} (ex-data error)]
    (if line
      (str "Line " line ", column " column ": " (ex-message error))
      (ex-message error))))

(defn compile-play-turn [code]
  (let [ctx (sci/fork sci-ctx)]
    (try
      (sci/eval-string* ctx code)
      (catch :default error
        (throw (ex-info (error-message error) {}))))
    (or (try
          (sci/eval-string* ctx "warrior.bot/play-turn")
          (catch :default _
            nil))
        (throw (ex-info "Define play-turn in the warrior.bot namespace" {})))))
