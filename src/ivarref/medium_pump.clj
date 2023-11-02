(ns ivarref.medium-pump
  (:require [aleph.http :as http]
            [aleph.tcp :as tcp]
            [clojure.edn :as edn]
            [manifold.stream :as s])
  (:import (java.net InetSocketAddress)))

(def cfg (edn/read-string (slurp "config.edn")))

(defn handler [sock _client-info]
  (let [websock @(http/websocket-client (get cfg :url)
                                        {:headers {"authorization" (get cfg :authorization)}})]
    (println "got socket")
    (s/connect sock websock)
    (s/connect websock sock)))

(defn start-server [_]
  (tcp/start-server
    (fn [sock client-info] (handler sock client-info))
    {:socket-address (InetSocketAddress. "localhost" 7777)}))

(defn start-pump
  [opts]
  (println "Starting server" opts)
  (with-open [server (start-server opts)]
    @(promise)))

;-Sdeps "{:deps {com.github.ivarref/medium-pump {:git/sha \"a5d145412a1f99dda5ae2085f9b5cfe319b5fca8\"}}
