package com.acbelter.chat.net.netty;

import org.jboss.netty.channel.Channel;

public class State {
    public static String getState(int state) {
        switch (state) {
            case (Channel.OP_NONE): {
                return "OP_NONE";
            }
            case (Channel.OP_READ): {
                return "OP_READ";
            }
            case (Channel.OP_WRITE): {
                return "OP_WRITE";
            }
            case (Channel.OP_READ_WRITE): {
                return "OP_READ_WRITE";
            }
            default: {
                return "UNDEFINED";
            }
        }
    }
}
