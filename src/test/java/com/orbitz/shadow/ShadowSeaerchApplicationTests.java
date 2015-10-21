package com.orbitz.shadow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShadowSeaerchApplication.class)
@WebAppConfiguration
public class ShadowSeaerchApplicationTests {

	@Test
	public void contextLoads() {
	}

}
