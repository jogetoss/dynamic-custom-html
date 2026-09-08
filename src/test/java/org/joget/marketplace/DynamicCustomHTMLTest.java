package org.joget.marketplace;

import java.util.regex.Matcher;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DynamicCustomHTMLTest {

    @Test
    public void matchesPlainToken() {
        Matcher matcher = DynamicCustomHTML.TOKEN_PATTERN.matcher("Your Full Address: {address}");
        assertTrue(matcher.find());
        assertEquals("address", matcher.group(1));
        assertEquals(null, matcher.group(2));
    }

    @Test
    public void matchesTokenWithSingleFormat() {
        Matcher matcher = DynamicCustomHTML.TOKEN_PATTERN.matcher("{address?nl2br}");
        assertTrue(matcher.find());
        assertEquals("address", matcher.group(1));
        assertEquals("?nl2br", matcher.group(2));
    }

    @Test
    public void matchesTokenWithChainedFormat() {
        Matcher matcher = DynamicCustomHTML.TOKEN_PATTERN.matcher("{address?nl2br;html}");
        assertTrue(matcher.find());
        assertEquals("address", matcher.group(1));
        assertEquals("?nl2br;html", matcher.group(2));
    }

    @Test
    public void matchesTokenWithParameterizedFormat() {
        Matcher matcher = DynamicCustomHTML.TOKEN_PATTERN.matcher("{address?separator(, )}");
        assertTrue(matcher.find());
        assertEquals("address", matcher.group(1));
        assertEquals("?separator(, )", matcher.group(2));
    }

    @Test
    public void noMatchWhenNoBraces() {
        Matcher matcher = DynamicCustomHTML.TOKEN_PATTERN.matcher("Your Full Address: address");
        assertFalse(matcher.find());
    }

    @Test
    public void noMatchForIdWithSpace() {
        Matcher matcher = DynamicCustomHTML.TOKEN_PATTERN.matcher("{addr ess}");
        assertFalse(matcher.find());
    }
}
