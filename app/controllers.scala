package controllers

import models.Albums
import play._
import play.mvc._
import templates.Template

object Application extends Controller {
  def index = Template

  def list(filter: String) = {
    //TODO
    Template("albums" -> Albums.findAll(/*filter*/))
  }

  def listByGenreAndYear(genre: String, year: String) = {
    Template("albums" -> Albums.findByGenreAndYear(genre, year))
  }

  def getYearsToDisplay(): List[Int] = {
    var years: List[Int] = List()
    val first = Albums.getFirstAlbumYear()
    val last = Albums.getLastAlbumYear()
    //TODO try to avoid the loop
    first.until(last).foreach(
      e => years = years.:+(e)
    )
    return years
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

