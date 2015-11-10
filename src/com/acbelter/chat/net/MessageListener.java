package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

/**
 * Слушает сообщения
 */
public interface MessageListener {
    void onMessage(Session session, Message message);
}
