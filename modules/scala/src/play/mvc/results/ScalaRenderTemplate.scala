package play.mvc.results

import play.mvc.ControllerDelegate
import play.mvc.Http
import play.mvc.Http


class ScalaRenderTemplate(template: String = null, args: java.util.Map[String, Object] = new java.util.HashMap[String,Object]) extends Result {

  val delegate = ControllerDelegate.renderTemplateForScala(template,args)

  def apply(request: Http.Request , response:Http.Response) {
    delegate.apply(request, response) 
  }

}
