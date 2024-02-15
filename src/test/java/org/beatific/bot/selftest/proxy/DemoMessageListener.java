package org.beatific.bot.selftest.proxy;

import org.springframework.stereotype.Component;

@Component
public class DemoMessageListener extends MessageListener {

    private String ifid;

    @Override
    public synchronized char[] listen(String ifid) {
        
        return super.listen(ifid);
    }
    
}
