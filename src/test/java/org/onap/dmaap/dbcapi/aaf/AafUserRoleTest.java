package org.onap.dmaap.dbcapi.aaf;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AafUserRoleTest {

    @Test
    public void toJSON() {
        AafUserRole role = new AafUserRole("test","admin");
        assertThat(role.toJSON(), is(" { \"user\": \"test\", \"role\": \"admin\" }"));
    }
}