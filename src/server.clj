(ns server
  (:require
    [hiccup.core :refer [html]]
    [hiccup.page :refer [html5 include-js]]
    [org.httpkit.server :as server]))


(defn hello-page
  []
  (html5
    [:head
     [:title "Hello niarv!"]
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     ;; Include Scittle
     (include-js "https://cdn.jsdelivr.net/npm/scittle@0.6.22/dist/scittle.js")
     ;; Include Reagent if you want to use it
     (include-js "https://cdn.jsdelivr.net/npm/scittle@0.6.15/dist/scittle.reagent.js")
     [:script {:type "application/x-scittle"}
      "
       (def app-element (.getElementById js/document \"app\"))
       (js/console.log \"Hello from ClojureScript!\")
       (set! (.-innerHTML app-element) \"Hello from ...\")
       (defn hello-world []
         (js/alert \"You clicked!\")
         (set! (.-innerHTML app-element) \"Hello from niarv.com!\"))
       ;; export function to use from JavaScript:
       (set! (.-hello_world js/window) hello-world)
       "]]
    [:body
     [:div
      [:h1 "Hello, World!"]
      [:p "This is a Babashka web application."]
      [:div#app "Loading..."]]
     [:button {:onclick "hello_world()"} "no"]]))


(defn handler
  [req]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (hello-page)})


(defn -main
  []
  (let [port 3000]
    (server/run-server handler {:port port})
    (println (str "Server started on port " port))
    @(promise)))


(-main)
