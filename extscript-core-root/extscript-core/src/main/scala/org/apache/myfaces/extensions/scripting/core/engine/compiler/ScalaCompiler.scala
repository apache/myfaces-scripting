/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.scripting.core.engine.compiler

import java.io.File
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext
import scala.tools.nsc.{Global, Settings}
import scala.collection.JavaConversions._
import org.apache.myfaces.extensions.scripting.core.common.util.{ClassUtils, FileUtils}

/**
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

class ScalaCompiler extends org.apache.myfaces.extensions.scripting.core.engine.api.Compiler
{
  def error(message: String): Unit =
  {
    println("[EXT-SCRIPTING] Error in scala compile:" + message)
  }

  def compile(sourcePath: File, targetPath: File, classLoader: ClassLoader): CompilationResult =
  {
    val context = WeavingContext.getInstance()
    val configuration = context.getConfiguration()
    targetPath.mkdirs();
    val sourceFiles = FileUtils.fetchSourceFiles(sourcePath, "*.scala")
    var sourceFileNames = List[String]()
    for (sourceFile:File <- sourceFiles)
    {
      sourceFileNames = sourceFileNames ::: List(sourceFile.getAbsolutePath())
    }
    val settings = new Settings(error)
    settings.outdir.value = configuration.getCompileTarget.getAbsolutePath
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings
    var cp: String = System.getProperty("java.class.path")


    if(!cp.contains("scala")) { //probably a war container
      val classesDir = ClassUtils.getContextClassLoader().getResource("./").getFile();

      val libDir = classesDir+".."+File.separator+"lib"
      val libs = FileUtils.fetchSourceFiles(new File(libDir),"*.jar")
      val finalPath = new StringBuilder
      finalPath.append(cp)
      finalPath.append(File.pathSeparator)
      finalPath.append(classesDir)
      for(singleLib:File <- libs){
        finalPath.append(File.pathSeparator)
        finalPath.append(singleLib.getAbsolutePath)
      }
      cp = finalPath.toString()
    }
    settings.classpath.value = cp
    val reporter = new CompilationResultReporter(settings)

    val compiler = new Global(settings, reporter)
    (new compiler.Run).compile(sourceFileNames)

    reporter.result
  }
}
