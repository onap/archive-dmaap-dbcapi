package org.onap.dmaap.dbcapi.service;

import static com.att.eelf.configuration.Configuration.MDC_BEGIN_TIMESTAMP;
import static com.att.eelf.configuration.Configuration.MDC_ELAPSED_TIME;
import static com.att.eelf.configuration.Configuration.MDC_END_TIMESTAMP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.MDC;


public class StopWatch {

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final static SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
    private final static TimeZone utc = TimeZone.getTimeZone("UTC");

    private long startTimestamp;
    private long elapsedTime;

    static {
        isoFormatter.setTimeZone(utc);
    }

    public void start() {
        startTimestamp = System.currentTimeMillis();
        MDC.put(MDC_BEGIN_TIMESTAMP, isoFormatter.format(new Date(startTimestamp)));
    }

    public void stop() {
        long endTimestamp = System.currentTimeMillis();
        elapsedTime = endTimestamp - startTimestamp;
        MDC.put(MDC_END_TIMESTAMP, isoFormatter.format(new Date(endTimestamp)));
        MDC.put(MDC_ELAPSED_TIME, String.valueOf(elapsedTime));
    }
    public void resetElapsedTime() {
        elapsedTime = 0;
		}

    public long getElapsedTime() {
        return elapsedTime;
    }
}
