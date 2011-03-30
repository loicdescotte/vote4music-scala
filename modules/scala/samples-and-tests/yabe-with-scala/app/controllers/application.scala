package controllers

import play._
import play.mvc._
import play.libs._
import play.cache._
import play.data.validation._

import models._

/**
 * Add default values to all request
 */
trait Defaults extends Controller {
    
    @Before def setDefaults {
        renderArgs += "blogTitle" -> configuration("blog.title")
        renderArgs += "blogBaseline" -> configuration("blog.baseline") 
    }
    
}

object Application extends Controller with Defaults {
 
    def index = {
        val frontPost = Posts.find("order by postedAt desc").first.orNull
        val olderPosts = Posts.find("from Post order by postedAt desc").from(1).fetch
        Template(frontPost, olderPosts)
    }
    
    def show(id: Long) = { 
        val post = Posts.findById(id).getOrNotFound
        val randomID = Codec.UUID
        Template(post, randomID)
    }
    
    def postComment(
        postId: Long, 
        @Required(message="Author is required") author: String, 
        @Required(message="A message is required") content: String, 
        @Required(message="Please type the code") code: String, 
        randomID: String
    ) = {
        val post = Posts.findById(postId).getOrNotFound
        
        Play.id match {            
            case "test" => // skip validation
            case _ => validation.equals(code, Cache.get(randomID).orNull) message "Invalid code. Please type it again"
        }  
        
        if(Validation.hasErrors) {
            "@show".asTemplate(post, randomID)
        } else {
            post.addComment(author, content)        
            flash.success("Thanks for posting %s", author)        
            Action(show(postId))
        }
        
    }
    
    def captcha(id: String) = {
        val captchaInstance = Images.captcha
        val code = captchaInstance.getText("#E4EAFD")
        Cache.set(id, code, "30min")
        captchaInstance
    }
    
    def listTagged(tag: String) = {
        val posts = Posts.findTaggedWith(tag)
        Template(tag, posts)
    }
 
}
