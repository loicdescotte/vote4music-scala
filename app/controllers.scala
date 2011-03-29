package controllers

import play._
import play.mvc._
import templates.Template
import java.security.Security

object Application extends Controller {
  def index = Template

  def list(filter: String) = {
    Template("albums" -> Albums.findAll(filter))
  }

  def listByGenreAndYear (genre: String, year:String) = {
    Template("albums" -> Albums.findByGenreAndYear(genre, year), "genre" -> genre, "year" -> year)
  }

}

object Admin extends Controller /*with Secure*/{

  /**
   * Log in
   */
  def login() = {
    Application.list(null)
  }

  //@Check("admin")
  def delete(id: Long) = {
    val album = Albums.findById(id)
    album.delete()
    Application.list(null)
  }

  //@Check("admin")
  def update(id: Long) = {
    val album = Albums.findById(id)
    render("@Application.form", album)
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