package org.beatific.bot.selftest.proxy;

public class EaiMQApi {

    int i =0 ;
    public int mq_connect() {
        return 0;
    }

    public int mq_get(EaiApiData apiData) {

        i++;
        if(i < 3) return 0;
        else return 1;
    }

    public int mq_commit() {
        return 0;
    }

    public int mq_rollback() {
        return 0;
    }

    public int mq_disconnect() {
        return 0;
    }
    
}
