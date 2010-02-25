package org.apache.myfaces.extensions.scripting.monitor.resources.file;

import org.apache.myfaces.extensions.scripting.monitor.resources.Resource;
import org.apache.myfaces.extensions.scripting.monitor.resources.ResourceResolver;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * <p>Test class for
 * <code>org.apache.myfaces.extensions.scripting.monitor.resources.file.FileSystemResourceResolver</code>.</p>
 */
public class FileSystemResourceResolverTest {

    // ------------------------------------------ Test methods

    /**
     * <p>Tests whether the resource resolver stops looking for further resources
     * if one callback tells it to do so.</p>
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBreakScanning() throws Exception {
        ResourceResolver resourceResolver = new FileSystemResourceResolver(
                new File(getClass().getResource(".").getFile()));
        resourceResolver.resolveResources(new BreakResourceCallback());
    }

    // ------------------------------------------ Utility classes

    private class BreakResourceCallback implements ResourceResolver.ResourceCallback {

        /** Flag that tells us whether this callback has already been called before. */
        boolean shallContinue = true;

        /**
         * <p>Callback method that will be invoked by a resource resolver
         * once it finds another resource that should be handled.</p>
         *
         * @param resource the resource encountered by the resource resolver
         */
        public boolean handle(Resource resource) {
            if (!shallContinue) {
                fail("The resource resolver called this callback even though it was told " +
                        "to stop looking for further resources previously.");
            } else {
                // Tell the resource resolver to stop looking for further resources
                // by returning false.
                shallContinue = false;
            }

            return shallContinue;
        }
    }

}
