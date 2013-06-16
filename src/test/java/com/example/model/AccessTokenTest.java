package com.example.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class AccessTokenTest extends AppEngineTestCase {

    private AccessToken model = new AccessToken();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
