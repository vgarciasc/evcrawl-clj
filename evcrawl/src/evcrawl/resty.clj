(ns evcrawl.resty
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [evcrawl.poordb :as db]
            [clojure.java.io]
            [clojure.walk :as walk])
  (:gen-class))

(defn list-books
  [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (db/list-books)})

(defn add-book [{body :body}]
  (let [body (walk/keywordize-keys body)]
    (db/add-book (:name body) (:tags body))
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (str "Succesfully added '" (:name body) "'")}))

(defn remove-book [{body :body}]
  (let [body (walk/keywordize-keys body)]
    (db/remove-book (:name body))
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (str "Succesfully removed '" (:name body) "'")}))

(defn update-book [{body :body}]
  (let [body (walk/keywordize-keys body)]
    (db/update-book (:old-name body)
                    (get-in body [:new-data :name])
                    (get-in body [:new-data :tags]))
    {:status  200
     :headers {"Content-Type" "text/json"}
     :body    (str "Succesfully updated '" (:old-name body) "'")}))

(defroutes app-routes
           (GET "/books" [] list-books)
           (POST "/books" [] add-book)
           (PUT "/books" [] update-book)
           (DELETE "/books" [] remove-book)
           (route/not-found "Error, page not found!"))

(defn -main
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server
      (wrap-json-body (wrap-json-response (wrap-cors app-routes
                                                     :access-control-allow-origin #".+"
                                                     :access-control-allow-methods [:get :put :post :delete])))
      {:port port})
    (println
      (str "Running webserver at http://127.0.0.1:" port "/"))))