package com.artisanplatform.ai.unit.controller;

import com.artisanplatform.ai.controller.AiJobController;
import com.artisanplatform.ai.service.AiJobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Controller-slice test for AiJobController. Add one @Test per endpoint
 * (success case + validation-failure case) as business logic is implemented —
 * see docs/architecture/09_TESTING_STRATEGY.md §3 for this module's full
 * happy-path/edge-case/failure-mode scenario list.
 */
@WebMvcTest(controllers = AiJobController.class)
class AiJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiJobService aiJobService;

    @Test
    void contextLoads() {
        // TODO: replace with real MockMvc.perform(...) calls per endpoint once
        // controller methods are implemented (currently stubbed with
        // UnsupportedOperationException — see AiJobController).
    }
}
