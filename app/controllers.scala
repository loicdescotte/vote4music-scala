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
  def yearsToDisplay(): List[Int] = {
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
   def form() = {
      Template
   }

  /**
   * vote for an album
   * @param id
   */
  def vote(id: String) = {
    //TODO try to use findById
    val album = Albums.find("id",id.toLong).fetch().apply(0)
    album.vote
    println("votes " + album.nbVotes)
    album.nbVotes
  }



}

