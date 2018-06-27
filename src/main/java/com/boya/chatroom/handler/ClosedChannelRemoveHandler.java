package com.boya.chatroom.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClosedChannelRemoveHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ClosedChannelRemoveHandler.class);

    private ConcurrentHashMap<String, SocketChannel> channelMap;
    private BlockingQueue<SocketChannel> toBeClosedChannel;

    public ClosedChannelRemoveHandler(ConcurrentHashMap<String, SocketChannel> channelMap, BlockingQueue<SocketChannel> toBeClosedChannel) {
        this.channelMap = channelMap;
        this.toBeClosedChannel = toBeClosedChannel;
    }

    private void remove() {

        Iterator<SocketChannel> socketChannelIterator = toBeClosedChannel.iterator();
        Iterator<String> channelMapIterator = channelMap.keySet().iterator();

        while (socketChannelIterator.hasNext()) {
            while (channelMapIterator.hasNext()) {
                String nickName = channelMapIterator.next();
                if (socketChannelIterator.next() == channelMap.get(nickName)) {
                    socketChannelIterator.remove();
                    channelMap.remove(nickName);
                }
            }
        }
    }

    @Override
    public void run() {
        remove();
        logger.info("ChannelMap updated at: " + LocalDateTime.now().toString());
    }
}
