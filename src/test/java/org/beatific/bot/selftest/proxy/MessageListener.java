package org.beatific.bot.selftest.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

// import com.eai.mq.api.EaiMQApi;
// import com.eai.mq.api.EaiApiData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageListener {

    private final String INTERFACE_ID = "TEST.COMM_EAI_MAU";

    protected void consume(String data) {
        log.info("test complete [{}]", data);
    }

    // @Scheduled(fixedDelay = 1000)
    @Test
    public synchronized void listen() {
        
        EaiMQApi mqapi = null;
        try {
            mqapi = new EaiMQApi();
            mqapi.mq_connect();
            EaiApiData apiData = new EaiApiData();
            apiData.initGet();
            int ret = 0;
            while (ret == 0) {
                try {
                    apiData.m_mqget_t.in_intf_id = INTERFACE_ID;
                    ret = mqapi.mq_get(apiData);
                    if( ret == 0) {
                        consume(apiData.m_mqget_t.out_recv_buf);
                    } else if(ret == -1 || ret == -2) {
                        log.error("MQ Message Not Found return : [{}], error Message : [{}]", ret, apiData.m_mqget_t.out_error_msg); 
                    } else {
                        throw new Exception("MQ Get Error return : [" + ret + "], error Message : ["+ apiData.m_mqget_t.out_error_msg + "]");
                    }
                } catch(Exception ex) {
                    log.error("MQ Error [{}]", ex.getMessage());
                }
            }
        } catch (Exception ex) {
            log.error("MQ Error [{}]", ex.getMessage());
        } finally {
            if(mqapi != null) mqapi.mq_disconnect();
        }
    }

}
