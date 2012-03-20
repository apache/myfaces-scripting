package org.apache.myfaces.extensions.scripting.core.engine.compiler

import scala.tools.nsc.Settings

import scala.tools.nsc.util._
import tools.nsc.reporters.AbstractReporter
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult
import org.apache.myfaces.extensions.scripting.core.engine.api.CompilationResult.CompilationMessage
import java.io.{ BufferedReader, IOException, PrintWriter }

/**
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * transforms the compile results from the scala compiler
 * to our own neutral compilation result facade
 */

class CompilationResultReporter(val settings: Settings)  extends AbstractReporter
{
  //TODO add summary with info etc...
  //for now this suffices to catch the errors
  val result = new CompilationResult("");



  /**Whether a short file name should be displayed before errors */
  var shortname: Boolean = false

  /**maximal number of error messages to be printed */
  val ERROR_LIMIT = 100

  private def label(severity: Severity): String = severity match
  {
    case ERROR => "error"
    case WARNING => "warning"
    case INFO => "info"
    case _ => ""
  }

  def display(pos: Position, msg: String, severity: Severity)
  {
    val theLabel = label(severity)
    severity match {
      case ERROR => result.registerError(new CompilationMessage(pos.line.longValue(),
        "Column: "+ pos.column +" "+msg));
      case WARNING => result.registerWarning(new CompilationMessage(pos.line.longValue(),"Column: "+ pos.column +" "+ msg));
      case INFO => ;
    }
  }

  def displayPrompt(): Unit = {}

}
