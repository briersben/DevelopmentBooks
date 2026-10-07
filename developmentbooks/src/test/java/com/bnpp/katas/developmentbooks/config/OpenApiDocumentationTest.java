package com.bnpp.katas.developmentbooks.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

	@Value("${developmentbooks.controller.path}${developmentbooks.endpoints.getbooks}")
	private String getBooksEndpoint;

	@Value("${developmentbooks.controller.path}${developmentbooks.endpoints.pricesummary}")
	private String fetchPriceSummaryEndpoint;

	@Value("${developmentbooks.controller.path}${developmentbooks.endpoints.getDiscountDetails}")
	private String getDiscountDetailsEndpoint;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("OpenAPI docs endpoint should expose all development books endpoints")
	void apiDocs_shouldExposeAllEndpoints() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().string(containsString(getBooksEndpoint)))
				.andExpect(content().string(containsString(fetchPriceSummaryEndpoint)))
				.andExpect(content().string(containsString(getDiscountDetailsEndpoint)));
	}

	@Test
	@DisplayName("Swagger UI should be reachable")
	void swaggerUi_shouldBeReachable() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
	}
}
