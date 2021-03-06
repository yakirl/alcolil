package org.yakirl.alcolil.common;

import org.junit.Test;
import org.yakirl.alcolil.common.Interval;
import org.yakirl.alcolil.common.Time;
import org.yakirl.alcolil.unittests.SuperTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TimeTest extends SuperTestCase{

    @Test
    public void testBasicTest() {
        try {
            Time failedInitTime= new Time("abcdef");
            fail("undefined time object created!");
        } catch (Exception e) {}
        
        Time time1 = new Time("2017-02-05 16:30:01.000 +0700");
        long seconds = time1.getSeconds();
        
        Time time2 = new Time(seconds);      
        assertEquals(time1, time2);
        LOG.info(String.format("%s. ", time1.equals(time2)));
        
        time1 = new Time("2017-02-05 16:30:01.555 +0700");
        time2 = new Time("2017-02-05 16:30:01.554 +0700");
        assertTrue(time2.before(time1));
        time1 = time1.minusMinutes(1);
        assertTrue(time1.before(time2));
        
        time2 = time1.minusMinutes(2);
        assertEquals(120L ,Time.durationInSeconds(time2, time1));   
        
        time1 = new Time("2017-02-05 16:30:01.555 +0700");
        // assertEquals("2017-02-05 16:30:01.555 +0700", time1.formattedString());        
        assertEquals(new Time("2017-02-05 16:30:00.000 +0700"), time1.roundToXMin(Interval.FIVE_MIN));
        
        time1 = new Time("2017-02-05 16:33:31.555 +0700");                
        assertEquals(new Time("2017-02-05 16:35:00.000 +0700"), time1.roundToXMin(Interval.FIVE_MIN));                                
        assertEquals(new Time("2017-02-05 16:34:00.000 +0700"), time1.roundToXMin(Interval.ONE_MIN));                
    }
}
