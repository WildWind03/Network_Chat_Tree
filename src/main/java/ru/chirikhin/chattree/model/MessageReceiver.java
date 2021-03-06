package ru.chirikhin.chattree.model;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class MessageReceiver implements Runnable {
    private static final Logger logger = Logger.getLogger(MessageReceiver.class.getName());
    private static final int SIZE_OF_DATAGRAM_PACKET = 2048;
    private static final int MAX_RANDOM_NUMBER = 100;

    private final BlockingQueue<ReceivedMessage> receivedMessages;
    private final DatagramSocket datagramSocket;
    private final int percentOfLoss;
    private final Random random = new Random();

    private final DatagramPacket datagramPacket = new DatagramPacket(new byte[SIZE_OF_DATAGRAM_PACKET], SIZE_OF_DATAGRAM_PACKET);

    public MessageReceiver(BlockingQueue<ReceivedMessage> receivedMessages, DatagramSocket datagramSocket, int percentOfLoss) {
        this.receivedMessages = receivedMessages;
        this.datagramSocket = datagramSocket;
        this.percentOfLoss = percentOfLoss;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                datagramSocket.receive(datagramPacket);
                logger.info("New message received");

                if (random.nextInt(MAX_RANDOM_NUMBER) >= percentOfLoss) {
                    logger.info("New message isn't lost!");
                    ReceivedMessage baseMessage = MessageFactory.createMessage(datagramPacket);
                    receivedMessages.put(baseMessage);
                } else {
                    logger.info("Unfortunately, the message is lost");
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("The thread was interrupted");
        }
    }
}
