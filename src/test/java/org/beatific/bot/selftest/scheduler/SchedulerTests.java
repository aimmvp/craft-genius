package org.beatific.bot.selftest.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class SchedulerTests {
    
    @Scheduled(cron="1 * * * * *") 
    public void scheduleAutoTest() {

        System.out.println("hello");
    }
}
