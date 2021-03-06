(ns sheetdb.subscribe
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.core.async
             :refer [>! <! >!! <!! put! take! go go-loop chan buffer close! thread
                     alts! alts!! timeout onto-chan pipeline]]))

(def sheet-key "1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w")

(defn- url [key]
  (str "https://spreadsheets.google.com/feeds/list/" key "/od6/public/values?alt=json"))

(defn- get-data
  "[sheet-key] Gets the entire spreadsheet with the given key"
  [sheet-key]
  (client/get (url sheet-key)))

(def data (json/read-str ((get-data sheet-key) :body)))

(def entries ((data "feed") "entry"))

(defn feed-chan [feed-url]
  "Gets the feed from the feed URL"
  (let [feed (thread (client/get feed-url))]
    feed))

; now to put on a channel for the core to consume
(def <entries (chan))

(defn wait-on-ch [ch]
  (go-loop []
    (let [e (<! ch)]
      (prn e))
    (recur)))


;;