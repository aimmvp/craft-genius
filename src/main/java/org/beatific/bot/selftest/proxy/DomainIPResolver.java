package org.beatific.bot.selftest.proxy;

import java.util.List;

public interface DomainIPResolver {
    
    public List<String> resolve(String domain);
}
