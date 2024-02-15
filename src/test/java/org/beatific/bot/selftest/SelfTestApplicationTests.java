package org.beatific.bot.selftest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootTest
class SelfTestApplicationTests {

	@Test
	void contextLoads() {
	}

	@Scheduled(cron="1 * * * * *") 
    public void scheduleAutoTest() {

        System.out.println("hello");
    }

}
