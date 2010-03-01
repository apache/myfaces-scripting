package org.apache.myfaces.extensions.scripting.dependencyScan;

import org.apache.myfaces.scripting.api.ScriptingConst;
import org.apache.myfaces.scripting.core.dependencyScan.filter.StandardNamespaceFilter;
import org.apache.myfaces.scripting.core.dependencyScan.filter.WhitelistFilter;
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

        assertFalse("Standard namespace is not allowed", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "java.lang.String"));
        assertTrue("Non Standard namespace is not allowed", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "booga.looga"));
    }

    @Test
    public void testWhiteList() {
        List<String> whiteList = new LinkedList<String>();
        whiteList.add("com.booga.booga1");
        whiteList.add("booga2");

        WhitelistFilter filter = new WhitelistFilter(whiteList);
        assertTrue("Whitelist test 1", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "com.booga"));
        assertTrue("Whitelist test 2", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "com.booga.booga1"));
        assertTrue("Whitelist test 3", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "booga2"));

        assertFalse("Whitelist test 4", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "com.booga1"));
        assertFalse("Whitelist test 5", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "org.booga1"));
        assertFalse("Whitelist test 6", filter.isAllowed(ScriptingConst.ENGINE_TYPE_JAVA, "aaa"));

    }

}
