package org.apache.myfaces.scripting.facelet;

import com.sun.facelets.impl.DefaultResourceResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * decorated Facelet resource resolver to reroute
 * the resource requests to our source path if possible
 */
public class ReroutingResourceResolver extends DefaultResourceResolver {

    DefaultResourceResolver _delegate = new DefaultResourceResolver();
    volatile boolean _initiated = false;
    List<String> _resourceDirs = null;


    Log log = LogFactory.getLog(this.getClass());

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
                    log.error(e);
                }
            }
        }

        return _delegate.resolveUrl(path);
    }
}

