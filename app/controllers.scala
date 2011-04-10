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

}

