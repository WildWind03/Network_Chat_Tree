package ru.chirikhin.chattree.model;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

public class MessageSender implements Runnable {
    private static final Logger logger = Logger.getLogger(MessageSender.class.getName());

    private final BlockingQueue<AddressedMessage> addressedMessages;
    private final DatagramSocket datagramSocket;

    public MessageSender(BlockingQueue<AddressedMessage> addressedMessages, DatagramSocket datagramSocket) {
        this.addressedMessages = addressedMessages;
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                AddressedMessage addressedMessage = addressedMessages.take();
                BaseMessage baseMessage = addressedMessage.getBaseMessage();

                logger.info("Trying to send message " + baseMessage.getGlobalID() + " to: " + addressedMessage.getReceiverAddress().getHostName() + ":" + addressedMessage.getReceiverAddress().getPort());
                InetSocketAddress inetSocketAddress = addressedMessage.getReceiverAddress();
                datagramSocket.send(new DatagramPacket(baseMessage.bytes(), baseMessage.bytes().length, inetSocketAddress));
                logger.info ("The Message " + baseMessage.getGlobalID() + " was sent");
            }
        } catch (InterruptedException e) {
            logger.info ("Thread was interrupted");
        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
    }
}
