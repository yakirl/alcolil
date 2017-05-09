package org.gitprof.alcolil.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SuperTestCase extends TestCase {

    public SuperTestCase(String testName) {
        super(testName);
    }
    
    protected void assertEqualsMsg(Object obj1, Object obj2) {
        assertEquals(String.format("%s not equals to %s", obj1.toString(), obj2.toString()), obj1, obj2);
    }
}
