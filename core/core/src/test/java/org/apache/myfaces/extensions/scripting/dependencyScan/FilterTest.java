package org.apache.myfaces.extensions.scripting.dependencyScan;

import org.apache.myfaces.scripting.core.dependencyScan.StandardNamespaceFilter;
import org.apache.myfaces.scripting.core.dependencyScan.WhitelistFilter;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FilterTest {

    @Test
    public void testStandardNamespace() {
        StandardNamespaceFilter filter = new StandardNamespaceFilter();

        assertFalse("Standard namespace is not allowed", filter.isAllowed("java.lang.String"));
        assertTrue("Non Standard namespace is not allowed", filter.isAllowed("booga.looga"));
    }

    @Test
    public void testWhiteList() {
        List<String> whiteList = new LinkedList<String>();
        whiteList.add("com.booga.booga1");
        whiteList.add("booga2");

        WhitelistFilter filter = new WhitelistFilter(whiteList);
        assertTrue("Whitelist test 1", filter.isAllowed("com.booga"));
        assertTrue("Whitelist test 2", filter.isAllowed("com.booga.booga1"));
        assertTrue("Whitelist test 3", filter.isAllowed("booga2"));

        assertFalse("Whitelist test 4", filter.isAllowed("com.booga1"));
        assertFalse("Whitelist test 5", filter.isAllowed("org.booga1"));
        assertFalse("Whitelist test 6", filter.isAllowed("aaa"));

    }

}
