package play.mvc

import scala.xml.NodeSeq
import scala.io.Source

import java.io.InputStream
import java.util.concurrent.Future

import play.mvc.Http._
import play.mvc.Scope._
import play.data.validation.Validation
import play.classloading.enhancers.LocalvariablesNamesEnhancer.LocalVariablesSupport
import play.classloading.enhancers.ControllersEnhancer.ControllerSupport

/**
* utility class to provider an easier way to render argumetns
*/
private[mvc] class RichRenderArgs(val renderArgs: RenderArgs) {
    def +=(variable: Tuple2[String, Any]) {
        renderArgs.put(variable._1, variable._2)
    }
}

/**
* utility class to provide some extra syntatic sugar while dealing with a session
*/
private[mvc] class RichSession(val session: Session) {
    def apply(key: String) = {
        session.contains(key) match {
            case true => Some(session.get(key))
            case false => None
        }
    }
}

/**
* Wrap a String as template name
*/
private[mvc] class StringAsTemplate(val name: String) {
    def asTemplate(args: Any*) = new results.ScalaRenderTemplate(name,ScalaController.argsToParams(args: _*))
    def asTemplate = new results.ScalaRenderTemplate(name)
}

private[mvc] class OptionWithResults[T](val o: Option[T]) {
    def getOrNotFound: T = {
        o match {
            case Some(x) => o.get.asInstanceOf[T]
            case None => throw new results.NotFound("Not found")
        }
    }
}

/**
* utility class to provide some extra syntatic sugar while dealing with Response objects
*/
private[mvc] class RichResponse(val response: Response) {

    val ContentTypeRE = """[-a-zA-Z]+/[-a-zA-Z]+""".r

    def <<<(x: String) {
        x match {
            case ContentTypeRE() => response.contentType = x
            case _ => response.print(x)
        }
    }

    def <<<(header: Header) {
        response.setHeader(header.name, header.value())
    }

    def <<<(header: Tuple2[String, String]) {
        response.setHeader(header._1, header._2)
    }

    def <<<(status: Int) {
        response.status = status
    }

    def <<<(xml: scala.xml.NodeSeq) {
        response.print(xml)
    }
}


