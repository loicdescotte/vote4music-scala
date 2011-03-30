import scala.collection.mutable._
import play.scalasupport.core.OnTheFly

package object play_with_scala {
    
    def println(v: Any) {
        env.Env.out.get += v.toString
    }
    
    def print(v: Any) {
        println(v)
    }
    
}

package object interpreted {
    
    def println(v: Any) {
        env.Env.out.get += v.toString
    }
    
    def print(v: Any) {
        println(v)
    }
    
}

package env {
    
    object Env {        
        val out = new ThreadLocal[ListBuffer[String]]
    }
    
}

package controllers {
    

    import play.mvc._
    import play_with_scala._

    object Application extends Controller {
    
        def interactive(script: String = "println(\"hello scala!\")") = {
              env.Env.out set ListBuffer[String]()
              if(request.method == "POST" && script != null) {
                try {
                synchronized{
                 OnTheFly.eval(script) 
                 }
                } catch { 
                  case e: Exception => "interactive_results.html".render("error" -> e, "trace" -> getStackTrace(e), "script" -> script) 
                }
              }
            "interactive_results.html".asTemplate("results" -> env.Env.out.get, "script"->script)
        } 

        def index {
            env.Env.out set ListBuffer[String]()
            val c = Class.forName("play_with_scala.Scrapbook")
            c.newInstance()
            "results.html".asTemplate("results" -> env.Env.out.get)
        } 
  

        private def getStackTrace(aThrowable: Throwable ) = {
          import java.io._
          val result = new StringWriter
          val printWriter = new PrintWriter(result)
          aThrowable.printStackTrace(printWriter)
          result.toString
        }
    }

    
}

