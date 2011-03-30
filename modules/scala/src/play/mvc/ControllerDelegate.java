package play.mvc;

import play.mvc.Controller;
import play.mvc.Router.ActionDefinition;
import play.exceptions.UnexpectedException;
import play.mvc.results.RenderTemplate;

import java.io.InputStream;
import java.io.File;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Future;


/**
 * 
 * provides java interop
 */
public abstract class ControllerDelegate {

    // ~~~~ 
    
    public static ActionDefinition reverseForScala() {
        return Controller.reverse();
    }

    public ActionDefinition reverse() {
        return Controller.reverse();
    }
    
    public static RenderTemplate renderTemplateForScala(String template, Map<String,Object> args) {
	try{    
	  if (template == null)
	    Controller.renderTemplate(args);
	  else
            Controller.renderTemplate(template, args);
        } catch(Throwable t) {
            if(t instanceof RenderTemplate) {
                return (RenderTemplate)t;
            }
            if(t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new UnexpectedException(t);
        }
        return null;
    }
    
}
