package org.apache.myfaces.scripting.loaders.java.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.scripting.core.util.Strategy;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *          <p/>
 *          Java strategy pattern to filter out all fully qualified classes
 *          under a given dir (excluding the inner classes)
 */

public class PackageStrategy
        implements Strategy

{
    String rootDir = "";
    Set<String> _foundFiles;
    Pattern rePattern;


    public PackageStrategy(Set<String> target, String pattern) {
        pattern = pattern.trim().replaceAll("\\.", "\\\\.");
        pattern = "." + pattern;

        _foundFiles = target;

        rePattern = Pattern.compile(pattern);

    }

    //only directories with classes in it apply to be valid
    //namespaces!

    public void apply(Object element) {
        File foundFile = (File) element;
        String fileName = foundFile.getName().toLowerCase();
        Matcher matcher = rePattern.matcher(fileName);

        if (!matcher.matches()) return;

        if (!foundFile.isDirectory()) {
            String relativePath = foundFile.getPath().substring(rootDir.length() + 1);
            relativePath = FilenameUtils.separatorsToUnix(relativePath);
            _foundFiles.add(relativePath.replaceAll("\\/", "."));
        }
    }

    public Set<String> getFoundFiles() {
        return _foundFiles;
    }

    public void setFoundFiles(Set<String> foundFiles) {
        _foundFiles = foundFiles;
    }
}
