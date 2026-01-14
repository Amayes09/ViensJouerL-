package com.example.messaging;

import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class JmsUserCreatedConsumer {

    private final ActiveMQConnectionFactory connectionFactory;
    private final Queue queue;

    private Thread thread;
    private volatile boolean running = false;

    public JmsUserCreatedConsumer(ActiveMQConnectionFactory connectionFactory, Queue queue) {
        this.connectionFactory = connectionFactory;
        this.queue = queue;
    }

    public synchronized void start() {
        if (running) return;
        running = true;

        thread = new Thread(() -> {
            try (JMSContext context = connectionFactory.createContext()) {
                JMSConsumer consumer = context.createConsumer(queue);
                System.out.println("👂 Consumer JMS actif sur 'UserCreatedQueue'");

                while (running && !Thread.currentThread().isInterrupted()) {
                    // timeout pour pouvoir arrêter proprement
                    String message = consumer.receiveBody(String.class, 1000);
                    if (message != null) {
                        System.out.println("📥 [REÇU] Artemis a transmis : " + message);
                    }
                }
            } catch (Exception e) {
                System.out.println("🛑 Arrêt du consumer JMS");
            }
        }, "jms-usercreated-consumer");

        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }
}
