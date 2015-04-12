;;;
;;;   Copyright 2014, University of Applied Sciences Frankfurt am Main
;;;
;;;   This software is released under the terms of the Eclipse Public License 
;;;   (EPL) 1.0. You can find a copy of the EPL at: 
;;;   http://opensource.org/licenses/eclipse-1.0.php
;;;

(ns
  ^{:author "Ruediger Gad",
    :doc "JMS controller for ActiveMQ."} 
  clj-jms-activemq-toolkit.ActiveMqJmsController
  (:gen-class
   :implements [clj_jms_activemq_toolkit.JmsController]
   :init init
   :constructors {[String] []}
   :methods [[startEmbeddedBroker [] void]
             [stopEmbeddedBroker [] void]]
   :state state)
  (:use clj-jms-activemq-toolkit.jms)
  (:import (clj_jms_activemq_toolkit JmsConsumer JmsController JmsProducer)))

(defn -init [jms-url]
  [[] {:jms-url jms-url :broker (ref nil)}])

(defn -createProducer [this topic-identifier]
  (let [producer (create-producer (:jms-url (.state this)) topic-identifier)]
    (proxy [JmsProducer] []
      (sendObject [obj]
        (producer obj)))))

(defn -connectConsumer [this topic-identifier consumer-impl]
  (create-consumer 
    (:jms-url (.state this)) 
    topic-identifier 
    (fn [obj]
      (.processObject consumer-impl obj))))

(defn -startEmbeddedBroker [this]
  (let [broker-ref (:broker (.state this))]
    (if (nil? @broker-ref)
      (let [brkr (start-broker (:jms-url (.state this)))]
        (dosync (ref-set broker-ref brkr))))))

(defn -stopEmbeddedBroker [this]
  (let [broker-ref (:broker (.state this))]
    (when (not (nil? @broker-ref))
      (.stop @broker-ref)
      (dosync (ref-set broker-ref nil)))))

