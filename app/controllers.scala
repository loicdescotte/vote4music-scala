package controllers

import play._
import data.validation.{Valid, Validation}
import play.mvc._
import templates.Template
import models.{Artist, Album, Albums}
import java.io.File

object Application extends Controller {
  def index = Template

  /**
   * Album list
   */
  def list(filter: String) = {
    //TODO
    Template("albums" -> Albums.findAll(/*filter*/))
  }

  /**
   * list albums by genre and year
   */
  def listByGenreAndYear(genre: String, year: String) = {
    Template("albums" -> Albums.findByGenreAndYear(genre, year))
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
  def save(@Valid album: Album, @Valid artist: Artist, cover: File): Unit = {
    if (Validation.hasErrors) {
      Template("@form", album)
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
    list(null)
  }

  /**
   * Just display the form
   */
   def form() = {
      Template
   }



}

