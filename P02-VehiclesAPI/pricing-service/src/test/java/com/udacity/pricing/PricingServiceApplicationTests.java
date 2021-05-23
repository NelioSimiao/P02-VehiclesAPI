package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricingServiceApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	@Test
	public void contextLoads() {
	}

	@Test
	public void findPrice() throws Exception {
		mockMvc.perform(get("/services/price").param("vehicleId", "1"))
				.andExpect(status().isOk());
	}

	@Test
	public void findPriceNotFoud() throws Exception {
		mockMvc.perform(get("/services/price").param("vehicleId", "855"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getPrice() {
		ResponseEntity<Price> response = this.restTemplate.getForEntity("http://localhost:" + port + "/prices/1", Price.class);
		Assert.assertNotNull(response);
		Assert.assertEquals(java.util.Optional.ofNullable(response.getBody().getVehicleId()), Optional.of(1L));

	}


}
