package org.beatific.bot.selftest.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {
    
    @Scheduled(cron="1 * * * * *") 
    public void scheduleAutoTest() {

    }
}
