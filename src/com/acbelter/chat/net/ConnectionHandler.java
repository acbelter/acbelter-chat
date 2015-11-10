package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;

import java.io.IOException;

/**
 * Обработчик сокета
 */
public interface ConnectionHandler extends Runnable {
    void send(Message msg) throws IOException;
    void addListener(MessageListener listener);
    void stop();
}
