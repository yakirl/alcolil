package org.gitprof.alcolil.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.gitprof.alcolil.unittests.SuperTestCase;

public class TimeTest extends SuperTestCase{

    @Test
    public void testBasicTest() {
        try {
            ATime failedInitTime= new ATime("abcdef");
            fail("undefined time object created!");
        } catch (Exception e) {}
        
        ATime time1 = new ATime("2017-02-05 16:30:01.000 +0700");
        long seconds = time1.getSeconds();
        
        ATime time2 = new ATime(seconds);      
        assertEquals(time1, time2);
        LOG.info(String.format("%s. ", time1.equals(time2)));
        
        time1 = new ATime("2017-02-05 16:30:01.555 +0700");
        time2 = new ATime("2017-02-05 16:30:01.554 +0700");
        assertTrue(time2.before(time1));
        time1 = time1.minusMinutes(1);
        assertTrue(time1.before(time2));
        
        time2 = time1.minusMinutes(2);
        assertEquals(120L ,ATime.durationInSeconds(time2, time1));   
        
        time1 = new ATime("2017-02-05 16:30:01.555 +0700");
        // assertEquals("2017-02-05 16:30:01.555 +0700", time1.formattedString());        
        assertEquals(new ATime("2017-02-05 16:30:00.000 +0700"), time1.roundToXMin(AInterval.FIVE_MIN));
        
        time1 = new ATime("2017-02-05 16:33:31.555 +0700");                
        assertEquals(new ATime("2017-02-05 16:35:00.000 +0700"), time1.roundToXMin(AInterval.FIVE_MIN));                                
        assertEquals(new ATime("2017-02-05 16:34:00.000 +0700"), time1.roundToXMin(AInterval.ONE_MIN));                
    }
}
