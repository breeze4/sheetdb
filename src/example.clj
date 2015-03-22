(ns example
  (:require [sheetdb.core :as db]
            [clojure.core.async :refer [<! go-loop]]))

; example:
; call a single function (db/start-db feed-key polling-interval)
; todo:
;  - polling-interval is the polling interval in milliseconds
;  - feed-key or feed-url is the google spreadsheet feed
;  - shutdown gracefully
;  - look into calling from Java as an example


(def sheet-key "1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w")

(def <db (db/start-db sheet-key 60000))

(defn fetch-data [<db]
  (go-loop []
    (let [data (<! <db)]
      (prn "Received update: " data))
    (recur)))


;;


