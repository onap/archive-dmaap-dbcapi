package org.onap.dmaap.dbcapi.resources;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.dmaap.dbcapi.model.Feed;


public class TestFeedCreator {


    private final FastJerseyTestContainer testContainer;

    public TestFeedCreator(FastJerseyTestContainer testContainer) {
        this.testContainer = testContainer;
    }

    Feed addFeed(String name, String desc) {
        Feed feed = new Feed(name, "1.0", desc, "dgl", "unrestricted");
        Entity<Feed> reqEntity = Entity.entity(feed, MediaType.APPLICATION_JSON);
        Response resp = testContainer.target("feeds").request().post(reqEntity, Response.class);
        int rc = resp.getStatus();
        System.out.println("POST feed resp=" + rc);
        assertTrue(rc == 200 || rc == 409);
        feed = resp.readEntity(Feed.class);
        return feed;
    }
}
