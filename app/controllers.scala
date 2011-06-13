package controllers

import models._
import play._
import mvc._
import play.data.validation.Validation
import play.i18n.Messages
import java.io.File

object Application extends Controller {

  import views.Application._

  def index = html.index()

  /**
   * Album list
   */
  def list() = {
    html.list(Album.findAll)
  }

  /**
   * Album list with filter
   */
  def search = {
    val filter = params.get("filter")
    html.list(Album.search(filter))
  }

  /**
   * list albums by genre and year
   */
  def listByGenreAndYear = {
    val genre = params.get("genre")
    val year = params.get("year")
    html.listByGenreAndYear(Album.findByGenreAndYear(genre, year), genre, year)
  }

  /**
   * Years of albums releases
   */
  def yearsToDisplay: List[Int] = {
    val first = Album.firstAlbumYear
    val last = Album.lastAlbumYear
    first.to(last).toList.reverse
  }

  /**
   * Create or update album
   *
   * @param album
   * @param artist
   * @param cover
   */
  def save(albumId:Option[Long]) = {
    val album = params.get("album", classOf[Album])
    val artist = params.get("artist", classOf[Artist])
    //forward if error
    Validation.valid("album",album)
    Validation.valid("artist",artist)
    if (Validation.hasErrors) {
      Action(form)
    }
    else {
      val artistId = Artist.findOrCreate(artist.name)
      album.artist_id = artistId
      //if new album (create mode)
      albumId match {
        case Some(id) => Album.update(album)
        case None => album.nbVotes = 0
                     Album.insert(album)
      }
      val cover = params.get("cover",classOf[File])
      if (cover != null) {
        val path: String = "/public/shared/covers/" + albumId
        album.hasCover = true
        val newFile: File = Play.getFile(path)
        if (newFile.exists) newFile.delete
        cover.renameTo(newFile)
        Album.update(album)
      }
      //forward to list action
      Action(list)
    }
  }

  /**
   * Just display the form
   */
  def form = html.edit(None, None)

  /**
   * vote for an album
   * @param id
   */
  def vote(id: String) = {
    Album.find("id = {c}").on("c" -> id).list().map(a => {
      a.vote()
      a.nbVotes
    })
  }


  /**
   * List albums in xml or json format
   *
   * @param genre
   * @param year
   */
/*
  def listByApi(genre: String, year: String) = {
    var albums: Seq[Album] = null;
    if (genre != null) {
      albums = Album.find("genre like {g}").on("g" -> genre.toUpperCase).list()
    } else {
      albums = Album.find().list()
    }
    if (year != null) {
      albums = Album.filterByYear(albums, year)
    }

    //TODO there is a bug in JSON serializer
     //if (request.format.equals("json"))
     //return Json(albums)
     
    //TODO templating not working with 'xml' yet
    //xml.listByApi(albums)
  }

*/

}

object Admin extends Controller with AdminOnly {


  import views.Application._

  /**
   * Log in
   */
  def login = {
    Action(Application.list)
  }

  /**
   * Delete album
   * @param id
   */
  def delete(id: Option[Long]) = {
    id.map(id => Album.delete("id={c}").onParams(id).executeUpdate())
    Action(Application.list)
  }

  /**
   * Update album
   * @param id
   */
  def form(id: Option[Long]) = {
    val album = id.flatMap( id => Album.find("id={id}").onParams(id).first())
    val artist = album.flatMap( album => Artist.find("id={id}").onParams(album.artist_id).first())
    html.edit(album, artist)
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

  import views.Authentication._

  def login = {
    html.login(flash)
  }

  def logout() = {
    session.clear()
    Action(Admin.login)
  }

  def authenticate() = {
      val username = params.get("username")
      val password = params.get("password")
      Play.configuration.getProperty("application.admin").equals(username) &&
      Play.configuration.getProperty("application.adminpwd").equals(password) match {
      case true => session.put("username", username)
                   Action(Application.index)
      case false => flash.error(Messages.get("error.login"))
                    html.login(flash)
    }
  }

}



