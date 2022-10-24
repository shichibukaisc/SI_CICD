package ca.alberta.services.sithdfca.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ca.alberta.services.sithdfca.Constants;
import ca.alberta.services.sithdfca.SITHDFCAApplication;
import ca.alberta.services.sithdfca.repositories.SubscriptionInfoJpaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@SpringBootTest(webEnvironment = WebEnvironment.MOCK,classes = SITHDFCAApplication.class)
@AutoConfigureMockMvc
public class SubscriptionInfoTest {
    @Autowired
    private SubscriptionInfoJpaRepository repository;
    
    @Autowired
    private MockMvc mvc;
    
    @BeforeAll
    static void setup() {
        log.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        log.info("@BeforeEach - executes before each test method in this class");
    }

    @DisplayName("Single test successful")
    @Test
    void testSingleSuccessTest() {
        log.info("Success");
    }

    @Test
    @Disabled("Not implemented yet")
    void testShowSomething() {
    }
    
    @Test
    public void getAllSubscriptions_sizeGreaterThanZero() throws Exception {
        List<SubscriptionInfo> subscriptions = repository.findAll();
        assertTrue(subscriptions.size() > 0);
    }
    
    @Test void getHeaders_success() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/v1/public/headers").contentType(MediaType.APPLICATION_JSON))
    		.andExpect(status().isOk());
    }

    
    @Test void getHealth_success() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/health").contentType(MediaType.APPLICATION_JSON))
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.status", is("UP")));
    }
    
    @Test void getSubscriptions_unauthenticated_return_403() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/v1/subscriptions").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    	
    }

    @WithMockUser(authorities = {Constants.USER_ROLE, Constants.ADMIN_ROLE})
    @Test @Disabled("broken") void getSubscriptions_authenticated_return_200() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/v1/subscriptions").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(HttpStatus.OK.value()));
    	
    }	
    
    @AfterEach
    void tearDown() {
        log.info("@AfterEach - executed after each test method.");
    }

    @AfterAll
    static void done() {
        log.info("@AfterAll - executed after all test methods.");
    }
    
    

}
