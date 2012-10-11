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

import scala.tools.nsc.Settings

import scala.tools.nsc.util._
import tools.nsc.reporters.AbstractReporter
import java.io.{ BufferedReader, IOException, PrintWriter }
import org.apache.myfaces.extensions.scripting.core.engine.api.{CompilationMessage, CompilationResult}

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
