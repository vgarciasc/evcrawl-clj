(ns ^:figwheel-hooks encrud.core
  (:require
    [clojure.string :as s]
    [goog.dom :as gdom]
    [reagent.core :as r :refer [atom]]
    [ajax.core :refer [GET POST DELETE PUT]]))

(def ^:dynamic *base-url* "http://localhost:3000")

(defonce books (r/atom []))
(defonce new-book (r/atom {:name "default name"
                           :tags "tag1 tag2"}))

(defn swap-in!
  [atom field ev]
  (swap! atom assoc-in [(keyword field)] (-> ev .-target .-value)))

(defn update-list []
  (GET (str *base-url* "/books")
       {:response-format :json
        :keywords?       true
        :handler         #(reset! books %)}))

(defn add-to-list []
  (POST (str *base-url* "/books")
        {:format  :json
         :params  @new-book
         :handler #(update-list)}))

(defn remove-from-list
  [book]
  (DELETE (str *base-url* "/books")
          {:format  :json
           :params  book
           :handler #(update-list)}))

(defn update-book [old-name book]
  (PUT (str *base-url* "/books")
       {:format  :json
        :params  {:old-name old-name
                  :new-data book}
        :handler #(update-list)}))

(defn input-field [field]
  [:div {:class "input-field-div"}
   [:p (str field ":")]
   [:input {:type         "text"
            :class        "book-input-field"
            :defaultValue ((keyword field) @new-book)
            :on-change    #(swap-in! new-book field %)}]])

(defn insertion-component []
  [:div {:class "insertion-div"}
   [:div
    (for [field ["name" "tags"]]
      ^{:key field} (input-field field))]
   [:button.btn.btn-secondary
    {:on-click add-to-list}
    "add"]])

(defn display-book
  [book prices time]
  (let [editing (r/atom false)
        edit-values (r/atom book)]
    (fn [book prices time]
      [:tr
       [:td (if @editing
              [:textarea {:defaultValue (:name book)
                          :on-change    #(swap-in! edit-values :name %)}]
              (:name book))]
       [:td (if @editing
              [:textarea {:defaultValue (s/join " " (:tags book))
                          :on-change    #(swap-in! edit-values :tags %)}]
              (s/join ", " (:tags book)))]
       [:td
        (for [price (take 3 (sort prices))]
          ^{:key price} [:div
                         [:span (.toLocaleString price "pt-BR" #js {:style "currency" :currency "BRL"})]
                         [:br]])]
       [:td time]
       [:td.text-center
        (when (not @editing)
          [:button.btn.btn-sm.btn-secondary
           {:on-click #(reset! editing true)}
           "change"])
        (when @editing
          [:button.btn.btn-sm.btn-secondary
           {:on-click #(do (update-book (:name book) @edit-values)
                           (reset! editing false))}
           "save"])
        [:button.btn.btn-sm.btn-secondary
         {:on-click #(remove-from-list book)}
         "remove"]]])))

(defn list-component []
  [:div
   [:table.table.table-dark.table-striped.table-bordered.table-responsive
    [:thead
     [:tr
      [:th "name"]
      [:th "tags"]
      [:th "lowest prices"]
      [:th "last change"]
      [:th "control"]]]
    [:tbody
     (for [{:keys [book prices time]} @books]
       ^{:key (:name book)} [display-book book prices time])]]
   ])

(defn app []
  [:div
   [:h1 "evcrawler"]
   [insertion-component]
   [list-component]])

;; ----

(defn ^:after-load on-reload []
  (update-list)
  (r/render-component [app] (gdom/getElement "app")))

(on-reload)