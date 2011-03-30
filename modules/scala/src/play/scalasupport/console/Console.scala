package play.console

import scala.tools.nsc._
import java.io.File
import play.Play
import play.db.jpa.JPAPlugin
import jline.ConsoleReader
import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.interpreter.InteractiveReader

/**
* provides a simple REPL while keeping play's classpath
**/
object Console {
   def main(args : Array[String]) {
     val root = new File(System.getProperty("application.path"));
     Play.init(root, System.getProperty("play.id", ""));
     play.Logger.info("~")
     play.Logger.info("Starting up, please be patient")
     play.Logger.info("Ctrl+D to stop")
     play.Logger.info("~")
     play.Invoker.invokeInThread(new ConsoleThread())
     exit(0)
   }   		 
}

private class ConsoleThread extends play.Invoker.DirectInvocation {
  override def execute() {
    try {
      //launch readline loop using play's classloader
      Run.projectConsole(Play.classloader) 
    } catch {
     case e:Exception=> e.printStackTrace()
   }
  }
}
/**
* lifted from <a href="http://github.com/harrah/sbt/blob/master/src/main/scala/sbt/LineReader.scala">sbt</a>
* credit goes to Mark Harrah 
**/
private object JLine
{
  def terminal = jline.Terminal.getTerminal
  def createReader() =
    terminal.synchronized {
      val cr = new ConsoleReader
      terminal.enableEcho()
      cr.setBellEnabled(false)
      cr
    }
  def withJLine[T](action: => T) {
    val t = terminal
    t.synchronized
    {
      t.disableEcho()
      try { action }
      finally { t.enableEcho() }
    }
  }
}

/**
* modified after <a href="http://github.com/harrah/sbt/raw/3493aa528566a41a0a3bc781131f4f39c116a0ed/src/main/scala/sbt/Run.scala"> sbt</a>
* credit goes to Mark Harrah
*/
object Run {

  def projectConsole(classloader:ClassLoader) {
    createSettings { interpreterSettings =>
    createSettings { compilerSettings =>
      {
        JLine.withJLine {
          val loop = new ProjectInterpreterLoop(compilerSettings, classloader)
          loop.main(interpreterSettings)
        }
      }
    }}
  }
  /** Create a settings object and execute the provided function if the settings are created ok.*/
  private def createSettings(f: Settings => Unit)  {
    val command = new GenericRunnerCommand(Nil, message => play.Logger.error(message))
    if(command.ok)
      f(command.settings)
    else
      command.usageMsg
  }

  private class ProjectInterpreterLoop(compilerSettings: Settings,  classloader:ClassLoader) extends InterpreterLoop {
    override def createInterpreter()
    {
      compilerSettings.classpath.value = System.getProperty("java.class.path")
      in = InteractiveReader.createDefault()
      interpreter = new Interpreter(settings)
      {
        override protected def parentClassLoader = classloader
        override protected def newCompiler(settings: Settings, reporter: Reporter) = super.newCompiler(compilerSettings, reporter)
      }
      interpreter.setContextClassLoader()
      interpreter.interpret("import play._")
      interpreter.interpret("import play.db.jpa._")
      interpreter.interpret("import scala.collection.JavaConversions._")
    }
  }
}
