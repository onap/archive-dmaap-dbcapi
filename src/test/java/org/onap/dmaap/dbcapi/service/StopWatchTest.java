package org.onap.dmaap.dbcapi.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StopWatchTest {

    private StopWatch stopWatch = new StopWatch();

    @Test
    public void stopWatchShouldReturnElapsedTime() throws InterruptedException {
        stopWatch.start();
        Thread.sleep(1000);
        stopWatch.stop();
        assertTrue(stopWatch.getElapsedTime() > 1000 && stopWatch.getElapsedTime() < 2000);
    }

    @Test
    public void resetShouldSetElapsedTime() throws InterruptedException {
        stopWatch.start();
        Thread.sleep(1000);
        stopWatch.stop();
        stopWatch.resetElapsedTime();
        assertTrue(stopWatch.getElapsedTime()==0);
    }

}