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

}

object Admin extends Controller /*with Secure*/{

  /**
   * Log in
   */
  def login() = {
    Template("@Application.list")
  }

  //@Check("admin")
  def delete(id: Long) = {
    val album = Albums.findById(id)
    //TODO
    //Albums.delete()
    Template("@Application.list")
  }

  //@Check("admin")
  def update(id: Long) = {
    val album = Albums.findById(id)
    //TODO
    //Template("@Application.form", album)
  }
}
/*
trait Secure {
  self: Controller =>
  @Before
  def check =
    session("user") match {
      case name: String => info ("Logged as %s", name)
      _ => Security.login
      }
  }

*/