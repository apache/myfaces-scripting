package org.apache.myfaces.extensions.scripting.core.engine.compiler

import java.io.File
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult
import org.apache.myfaces.extensions.scripting.core.api.WeavingContext
import org.apache.myfaces.extensions.scripting.core.common.util.FileUtils
import scala.tools.nsc.{Global, Settings}
import scala.collection.JavaConversions._

/**
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */

class ScalaCompiler extends org.apache.myfaces.extensions.scripting.core.engine.api.Compiler
{
  def error(message: String):Unit =
  {
    println("[EXT-SCRIPTING] Error in scala compile:"+message)
  }

  def compile(sourcePath: File, targetPath: File, classLoader: ClassLoader): CompilationResult =
  {
    val context = WeavingContext.getInstance()
    val configuration = context.getConfiguration()
    targetPath.mkdirs();
    val sourceFiles = FileUtils.fetchSourceFiles(sourcePath, "*.scala")
    var sourceFileNames = List[String]()
    for (sourceFile:File <- sourceFiles) {
      sourceFileNames = sourceFileNames ::: List(sourceFile.getAbsolutePath())
    }

    val settings = new Settings(error)
    settings.outdir.value = configuration.getCompileTarget.getAbsolutePath
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings

    val reporter = new CompilationResultReporter(settings)

    val compiler = new Global(settings, reporter)
    (new compiler.Run).compile(sourceFileNames)

    reporter.result
  }
}
