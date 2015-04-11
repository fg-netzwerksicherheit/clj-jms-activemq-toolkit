;;;
;;;   Copyright 2014, University of Applied Sciences Frankfurt am Main
;;;
;;;   This software is released under the terms of the Eclipse Public License 
;;;   (EPL) 1.0. You can find a copy of the EPL at: 
;;;   http://opensource.org/licenses/eclipse-1.0.php
;;;

(ns 
  ^{:author "Ruediger Gad",
    :doc "Base namespace for tests with JMS functionality"}  
  clj-jms-activemq-toolkit.test.jms-test-base
  (:use clojure.test
        clj-assorted-utils.util
        clj-jms-activemq-toolkit.jms))

(def ^:dynamic *local-jms-server* "tcp://127.0.0.1:42424")
(def test-topic "/topic/testtopic.foo")

(defn run-test [t]
  (let [broker (start-broker *local-jms-server*)]
    (t)
    (.stop broker)))

(defn single-test-fixture [t]
  (run-test t)
  (binding [*local-jms-server* "udp://127.0.0.1:42426"]
    (run-test t))
  (binding [*local-jms-server* "stomp://127.0.0.1:42422"]
    (run-test t))
  (binding [*local-jms-server* "ssl://127.0.0.1:42425"
            *trust-store-file* "test/ssl/client.ts"
            *key-store-file* "test/ssl/client.ks"]
    (run-test t)))

