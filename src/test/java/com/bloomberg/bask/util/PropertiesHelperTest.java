package com.bloomberg.bask.util;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertiesHelperTest {

    Properties props;

    @Before
    public void setUp() {
        props = new Properties();
        props.setProperty("foo.bar.baz", "garply");
        props.setProperty("wibble.wobble.wubble", "waldo");
    }

    @Test
    public void testExtractConfigWorksWithEmptyString() {
        Properties result = PropertiesHelper.extractConfig("", props);
        assertEquals(result.getProperty("foo.bar.baz"), "garply");
        assertEquals(result.getProperty("wibble.wobble.wubble"), "waldo");
        assertEquals(result.size(), 2);
    }

    @Test
    public void testExtractConfigOnNonEmptyString() {
        Properties result = PropertiesHelper.extractConfig("wibble.wo", props);
        assertEquals(result.getProperty("bble.wubble"), "waldo");
        assertEquals(result.size(), 1);
    }

    @Test
    public void testValuesToListWorksWithNonCommaString() {
        List<String> result = PropertiesHelper.valuesToList("thisstringhasnocomma");
        assertEquals(result.get(0), "thisstringhasnocomma");
        assertEquals(result.size(), 1);
    }

    @Test
    public void testValuesToListWorksWithCommasWithSpaces() {
        List<String> result = PropertiesHelper.valuesToList("this,    string , has,commas");
        assertTrue(result.contains("this"));
        assertTrue(result.contains("string"));
        assertTrue(result.contains("has"));
        assertTrue(result.contains("commas"));
        assertEquals(result.size(), 4);
    }
}
