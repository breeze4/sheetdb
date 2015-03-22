(ns sheetdb.core
  (:require [sheetdb.poller :as poller]
            [sheetdb.subscribe :as sub]
            [clojure.core.async
             :refer [>! <! >!! <!! put! take! go go-loop chan buffer close! thread
                     alts! alts!! timeout onto-chan pipeline]]))

(def default-buffer 10)

(defn- url [key]
  (str "https://spreadsheets.google.com/feeds/list/" key "/od6/public/values?alt=json"))

(defn init-sub-url [feed-url polling-interval]
  (let [<feed (chan default-buffer)]
    (sheetdb.poller/start-poller feed-url <feed pool polling-interval)
    (sheetdb.poller/start-listener <feed)
    <feed))

(defn init-sub-key [feed-key polling-interval]
  (init-sub-url (url feed-key) polling-interval))

(defn init-sub [feed polling-interval]
  (if (.startsWith feed "http")
    (init-sub-url feed polling-interval)
    (init-sub-key feed polling-interval)))

(defn start-db
  ([feed]
   (start-db feed 0))
  ([feed polling-interval]
   (let [<out (init-sub feed polling-interval)]
     <out)))


;;