(ns paubox-clj.core
  (:require [clj-http.client :as http])
  (:use [clojure.data.json :only [json-str read-json]]
        [clojure.string :only [join]]))

(def client-version 0.1)
(def user-agent (format "clojure-%f" client-version))
(def api-base "/v1/")
(def host-base (str "https://api.paubox.net" api-base))
(def auth-header-format "Token token=%s")
(def client-header "X-PAUBOX-API-CLIENT")
(def ^{:dynamic true} *paubox-key* nil)
(def ^{:dynamic true} *user-name* nil)

(defrecord Request [path query body])
(defrecord Recipient [address name])
(defrecord Sender [address reply_to name])
(defrecord Content [text html])
(defrecord Attachment [fileName contentType content])
(defrecord Email [subject recipients ccs bccs sender non-tls? content attachments])

(defmacro with-paubox [key username & body]
  `(binding [*paubox-key* ~key
             *user-name* ~username]
     ~@body))

(defn- make-uri [request]
  (str host-base *user-name* (:path request)))

(defn- parameter-string [params]
  (join "&"
        (map (fn [[key val]] (str (name key) "=" (str val)))
             (sort-by #(name (key %)) java.lang.String/CASE_INSENSITIVE_ORDER params))))

(defn- make-headers []
  {"Content-Type" "application/json;charset=UTF-8"
   "Authorization" (format auth-header-format *paubox-key*)
   client-header user-agent})

(defn- do-get [request]
  (read-json (:body (http/get (make-uri request)
              {:body (:body request)
               :query-params (:query request)
               :headers (make-headers)}))))

(defn- do-post [request]
  (read-json
    (:body
      (http/post
        (make-uri request)
        {:body (json-str (:body request))
         :query-params (parameter-string (:query request))
         :headers (make-headers)}))))

(defn get-status
  "Gets PauBox API Status"
  ([] (do-get (Request. "/status" nil nil))))

(defn get-email-disposition
  "Gets a specific emails delivery statistics"
  ([email-tracking-id] (do-get (Request. "/message_receipt" {:sourceTrackingId email-tracking-id} nil))))

(defn build-email [email]
  (let [attachments (or (:attachments email) '[])]
  {:data { :message {
        :recipients (concat (:recipients email) (:ccs email))
        :bcc (:bccs email)
        :headers {
          :subject (:subject email)
          :from (:sender email)
          :reply-to (:sender email)
        }
        :allowNonTLS (:non-tls? email)
        :content {
          "text/plain" (:text (:content email))
          "text/html" (:html (:content email))
        }
        :attachments attachments
      }}}))

(defn send-email
  "Sends an encrypted email through PauBox"
  [email] 
  (let [formatted-email (build-email email)]
    (do-post (Request. "/messages" nil formatted-email))))