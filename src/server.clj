(ns server
  (:require
    [hiccup.page :refer [html5 include-js]]
    [babashka.fs :as fs]
    [org.httpkit.server :as server]))

(defn serve-static [req]
  (let [file-path (apply str (rest (:uri req)))]
    (if (fs/exists? file-path)
      {:status 200
       :headers {"Content-Type" (case (.substring file-path (inc (.lastIndexOf file-path ".")))
                                 "css" "text/css"
                                 "js" "text/javascript"
                                 "html" "text/html"
                                 "application/octet-stream")}
       :body (slurp file-path)}
      {:status 404
       :body "Not found"})))

(defn main-page
  []
  (html5
    [:head
     [:title "Hello niarv!"]
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     ;; Include Scittle
     (include-js "static/scittle.js")
     ;; Include Reagent if you want to use it
     (include-js "static/scittle.reagent.js")
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
  (if (.startsWith (:uri req) "/static")
    (serve-static req)
    {:status  200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body    (main-page)}))


(defn -main
  []
  (let [port 3000]
    (server/run-server handler {:port port})
    (println (str "Server started on port " port))
    @(promise)))


(-main)
