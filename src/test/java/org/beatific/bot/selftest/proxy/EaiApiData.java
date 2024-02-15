package org.beatific.bot.selftest.proxy;

public class EaiApiData {

    public MessageType m_mqget_t = new MessageType();

    public void initGet() {
    }

    public class MessageType {
        public String in_intf_id;
        public String out_recv_buf = "hello eai";
        public Object out_error_msg = "Error01";
    }
    
}
