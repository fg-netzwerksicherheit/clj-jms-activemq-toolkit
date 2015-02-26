;;;
;;;   Copyright 2014, Frankfurt University of Applied Sciences
;;;
;;;   This software is released under the terms of the Eclipse Public License 
;;;   (EPL) 1.0. You can find a copy of the EPL at: 
;;;   http://opensource.org/licenses/eclipse-1.0.php
;;;

(ns
  ^{:author "Ruediger Gad",
    :doc "Main class to start a simple ActiveMQ broker instance."}
  clj-jms-activemq-toolkit.main
  (:use cheshire.core
        clojure.pprint
        clojure.tools.cli
        clj-jms-activemq-toolkit.jms)
  (:gen-class))

(defn -main [& args]
  (let [cli-args (cli args
                      ["-u" "--url" 
                       "URL to bind the broker to." 
                       :default "tcp://localhost:61616"]
                      ["-d" "--daemon" "Run as daemon." :flag true :default false]
                      ["-h" "--help" "Print this help." :flag true])
        arg-map (cli-args 0)
        extra-args (cli-args 1)
        help-string (cli-args 2)]
    (if (arg-map :help)
      (println help-string)
      (do
        (println "Starting ActiveMQ Broker using the following options:")
        (pprint arg-map)
        (pprint extra-args)
        (let [url (arg-map :url)
              broker-service (start-broker url)
              broker-info-producer (create-producer url "/topic/broker.info.reply")
              broker-info-fn (fn [msg]
                               (if (= (type msg) java.lang.String)
                                 (let [m (parse-string msg)
                                       cmd (m "cmd")
                                       args (m "args")]
                                   (condp = cmd
                                     "get-destinations" (let [dst-vector (get-destinations broker-service)
                                                              dst-json (generate-json {"destinations" dst-vector})]
                                                          (broker-info-producer dst-json))
                                     (send-error-msg broker-info-producer (str "Invalid broker.info.cmd message: " msg))))
                                 (send-error-msg broker-info-producer (str "Invalid data type for broker.info.cmd message: " (type msg)))))
              broker-info-consumer (create-consumer url "/topic/broker.info.cmd" broker-info-fn)
              shutdown-fn (fn []
                            (broker-info-producer :close)
                            (broker-info-consumer :close)
                            (.stop broker-service))]
          ;;; Running the main from, e.g., leiningen results in stdout not being properly accessible.
          ;;; Hence, this will not work when run this way but works when run from a jar via "java -jar ...".
          (if (:daemon arg-map)
            (-> (agent 0) (await))
            (do
              (println "Broker started... Type \"q\" followed by <Return> to quit: ")
              (while (not= "q" (read-line))
                (println "Type \"q\" followed by <Return> to quit: "))
              (println "Shutting down...")
              (shutdown-fn))))))))

