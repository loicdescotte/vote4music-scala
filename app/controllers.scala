package controllers

import _root_.models.{Album, Artist, Albums}
import play._
import play.data.validation.{Valid, Validation}
import play.mvc._
import templates.Template
import java.io.File

object Application extends Controller {

  def index = Template

  /**
   * Album list
   */
  def list(filter: String) = {
    Template("albums" -> Albums.findAll(filter))
  }

  /**
   * list albums by genre and year
   */
  def listByGenreAndYear(genre: String, year: String) = {
    Template("albums" -> Albums.findByGenreAndYear(genre, year), "genre" -> genre, "year" -> year)
  }

  /**
   * Years of albums releases
   */
  def yearsToDisplay: List[Int] = {
    val first = Albums.firstAlbumYear()
    val last = Albums.lastAlbumYear()
    first.to(last).toList.reverse
  }

  /**
   * Create or update album
   *
   * @param album
   * @param artist
   * @param cover
   */
  def save(@Valid album: Album, @Valid artist: Artist, cover: File) = {
    //forward if error
    if (Validation.hasErrors) {
      //TODO does not work
      Action(form)
    }
    album.artist = artist
    album.replaceDuplicateArtist
    album.save
    if (cover != null) {
      val path: String = "/public/shared/covers/" + album.id
      album.hasCover = true
      val newFile: File = Play.getFile(path)
      if (newFile.exists) newFile.delete
      cover.renameTo(newFile)
      album.save
    }
    //forward to list action
    Action(list(null))
  }

  /**
   * Just display the form
   */
  def form =Template

  /**
   * vote for an album
   * @param id
   */
  def vote(id: String) = {
    val result = Albums.findById(id.toLong)
    result match {
      case None => 0
      case Some(album) => {
        album.vote
        album.nbVotes
      }
    }

  }

  //TODO There is a bug in JSON serializer
  def listByApi = Json("albums" -> Albums.findAll())

}


object Admin extends Controller with AdminOnly {

  /**
   * Log in
   */
  def login = {
    Action(Application.list(null))
  }

  /**
   * Delete album
   * @param id
   */
  def delete(id: Long) = {
    val result = Albums.findById(id)
    result match {
      case Some(album) => album.delete
    }
    Action(Application.list(null))
  }

  /**
   * Update album
   * @param id
   */
  def form(id: Long) = {
    val result = Albums.findById(id.toLong)
    result match {
      case Some(album) => {
        //TODO redirect to avoid template duplication
        Template("album"->album)
      }
  }

}

// Security
trait Secure extends Controller {

  @Before def check = {
    session("username") match {
      case Some(username) => Continue
      case None => Action(Authentication.login)
    }
  }

  @Util def connectedUser = session.get("username").asInstanceOf[String]

}

trait AdminOnly extends Secure {

  @Before def checkAdmin = {
    if (!connectedUser.equals("admin")) Forbidden else Continue
  }

}

object Authentication extends Controller {

  def login = Template

  def logout = {
    session.clear()
    flash.success("You have been disconnected")
    Action(Admin.login)
  }


  def authenticate(username: String, password: String) = {
    Play.configuration.getProperty("application.admin").equals(username) &&
      Play.configuration.getProperty("application.adminpwd").equals(password) match {
      case true => session.put("username", username)
      Action(Application.index)

      case false => flash.error("Oops, bad email or password")
      flash.put("username", username)
      Action(Admin.login)
    }
  }

}

}

