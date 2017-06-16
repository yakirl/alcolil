package org.gitprof.alcolil.unittests;

import org.gitprof.alcolil.global.Conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.assertEquals;

public class SuperTestCase {
            
    protected static final Logger LOG = LogManager.getLogger(SuperTestCase.class);
    protected static final String yahooQuotesTestFile1 = Conf.appendToTestResources("yahoo_GOOG_EXAMPLE_range4d_test1.txt");
    protected static final String yahooQuotesTestFile2 = Conf.appendToTestResources("yahoo_GOOG__EXAMPLE_range2d_test2.txt");
    protected double EPSILON = 0.0001;
    
    public SuperTestCase() {        
    }
    
    protected void assertEqualsDbl(double dbl1, double dbl2) {
        assertEquals(dbl1, dbl2, EPSILON);
    }
    
    public void testCheck() {
        
    }
}
