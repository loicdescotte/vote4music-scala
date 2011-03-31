package controllers

import models.Albums
import play._
import play.mvc._
import templates.Template
import java.security.Security

object Application extends Controller {
  def index = Template

  def list(filter: String) = {
    //TODO
    Template("albums" -> Albums.findAll(/*filter*/))
  }

  def listByGenreAndYear (genre: String, year:String) = {
    Template("albums" -> Albums.findAll())
    //TODO
    // findByGenreAndYear(genre, year), "genre" -> genre, "year" -> year)
  }

    def getYearsToDisplay(): Array[String]={
        /*
        for (int i = Album.getFirstAlbumYear(); i <= Album.getLastAlbumYear(); i++) {
            years.add(String.valueOf(i));
        }
        Collections.reverse(years);
        */
        Array("2010","2011")
    }

}

object Admin extends Controller with Secure {

  /**
   * Log in
   */
  def login() = {
    Template("@Application.list")
  }

  //@Check("admin")
  def delete(id: Long) = {
    for (n <- Albums.findById(id)) n.delete
    Template("@Application.list")
  }

  //@Check("admin")
  def update(id: Long) = {
    val album = Albums.findById(id)
    //TODO
    //Template("@Application.form", album)
  }
}


//Security
trait Secure extends Controller {

    @Before def check = {        
        session("user") match {
            case Some(email) => info(email + "logged")
            case None => Action(Authentication.login)
        }
    }

    //TODO
    //@Util def connectedUser = renderArgs.get("user").asInstanceOf[User]

}

trait AdminOnly extends Secure {

    @Before def checkAdmin = {
        //if(!connectedUser.isAdmin) Forbidden else Continue
        Continue
    }

}

object Authentication extends Controller {

    def login = Template

    def authenticate(username: String, password: String) = {
        /*
        Users.connect(username, password) match {
            case Some(u) => session.put("user", u.email)
                            Action(Admin.index)

            case None    => flash.error("Oops, bad email or password")
                            flash.put("username", username)
                            Action(login)
                            }
                            */
        
        //session.put("user", u.email)
        session.put("user", "admin@mail.com")
        Action(Application.index)
    }

    def logout = {
        session.clear()
        flash.success("You have been disconnected")
        //Action(login)
    }

}

