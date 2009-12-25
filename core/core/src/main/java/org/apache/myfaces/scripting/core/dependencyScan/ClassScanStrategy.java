package org.apache.myfaces.scripting.core.dependencyScan;

import org.apache.myfaces.scripting.core.util.ClassUtils;
import org.apache.myfaces.scripting.core.util.Strategy;
import org.apache.myfaces.scripting.core.util.WeavingContext;

import java.io.File;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * A scan strategy for scanning class files within our api
 */
public class ClassScanStrategy implements Strategy {
    Set<String> _whiteList;
    DependencyScanner _scanner;
    String _rootDir;


    public ClassScanStrategy(String rootDir, Set<String> whiteList, DependencyScanner scanner) {
        this._scanner = scanner;
        this._rootDir = rootDir;
        this._whiteList = whiteList;
    }

    public void apply(Object element) {
        File foundFile = (File) element;
        String fileName = foundFile.getName().toLowerCase();
        if (!fileName.endsWith(".class")) return;

        String className = ClassUtils.relativeFileToClassName(fileName.substring(_rootDir.length() + 1));
        Set<String> classDependencies = _scanner.fetchDependencies(className, _whiteList);

        WeavingContext.getFileChangedDaemon().getDependencyMap().addDependencies(className, classDependencies);
    }


}
