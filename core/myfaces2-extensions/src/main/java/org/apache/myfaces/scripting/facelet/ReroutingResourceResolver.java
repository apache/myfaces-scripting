package org.apache.myfaces.scripting.facelet;

import org.apache.myfaces.scripting.core.util.WeavingContext;
import org.apache.myfaces.view.facelets.impl.DefaultResourceResolver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * decorated Facelet resource resolver to reroute
 * the resource requests to our source path if possible
 */
public class ReroutingResourceResolver extends DefaultResourceResolver {

    DefaultResourceResolver _delegate = new DefaultResourceResolver();
    volatile boolean _initiated = false;
    List<String> _resourceDirs = null;

    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public URL resolveUrl(String path) {

        if (!_initiated) {
            _resourceDirs = WeavingContext.getConfiguration().getResourceDirs();
            _initiated = true;
        }

        if (_resourceDirs != null && !_resourceDirs.isEmpty()) {
            for (String resourceDir : _resourceDirs) {
                File resource = new File(resourceDir + path);
                if (resource.exists()) try {
                    return resource.toURI().toURL();
                } catch (MalformedURLException e) {
                    log.severe(e.toString());
                }
            }
        }

        return _delegate.resolveUrl(path);
    }
}
