package controllers

import models._
import play._
import play.data.validation.{Valid, Validation}
import play.mvc._
import play.i18n.Messages
import templates.Template
import java.io.File
import views.Application._


object Application extends Controller {

  def index = Template

  /**
   * Album list
   */
  def list() = {
    Template('albums -> Album.find("select * from Album a limit 100").list())
  }
  
  /**
   * Album list with filter
   */
  def search(filter: String) = {
    Template("@Application.list", 'albums -> Album.findAll(filter))
  }

  /**
   * list albums by genre and year
   */
  def listByGenreAndYear(genre: String, year: String) = {
    Template('albums -> Album.findByGenreAndYear(genre, year), 'genre -> genre, 'year -> year)
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
  def save(@Valid album: Album, @Valid artist: Artist, cover: File) = {
    //forward if error
    if (Validation.hasErrors) {
      Action(form)
    }
    else{
      //TODO creer si nouveau
      Album.insert(album)
      Artist.insert(artist);
      //album.replaceDuplicateArtist
      if (cover != null) {
        val path: String = "/public/shared/covers/" + album.id
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
  def form = Template

  /**
   * vote for an album
   * @param id
   */
  def vote(id: String) = {
    Album.find("id = {c}").on("c" -> id).list().map( a =>{
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
	def listByApi(genre:String, year:String) = {
      var albums : Seq[Album] = null;
      if (genre != null) {
          albums = Album.find("genre like {g}").on("g"->genre.toUpperCase).list()
      } else {
          albums = Album.find().list()
      }
      if (year != null) {
          albums = Album.filterByYear(albums, year)
      }

	  //TODO There is a bug in JSON serializer
	  /*
      if (request.format.equals("json"))
	  return Json(albums)
	  */
	  //TODO not working with 'xml' yet  
	  //xml.listByApi(albums)
  }
	
}


object Admin extends Controller with AdminOnly {

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
  def delete(id: Long) = {
    Album.find("id = {c}").on("c" -> id).list().map( a => {
        Album.delete("id={c}").onParams(id).executeUpdate()
        Action(Application.list)
    })
  }

  /**
   * Update album
   * @param id
   */
  def form(id: Long) = {
    Album.find("id = {c}").on("c" -> id).list().map( a =>
       Template("@Application.form", 'album -> a)
    )
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
    Action(Admin.login)
  }

  def authenticate(username: String, password: String) = {
    Play.configuration.getProperty("application.admin").equals(username) &&
      Play.configuration.getProperty("application.adminpwd").equals(password) match {
      case true => session.put("username", username)
      Action(Application.index)
      case false => flash.error(Messages.get("error.login"))      
      Template("@Authentication.login")
    }
  }

}



