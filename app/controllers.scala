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

  def yearsToDisplay(): List[Int] = {
    val first = Albums.firstAlbumYear()
    val last = Albums.lastAlbumYear()
    first.to(last).toList.reverse
  }


}

