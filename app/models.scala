package models

import java.util.Date
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm
import play.data.validation.Required
import java.text.SimpleDateFormat
import java.lang.Long


case class Album(
             var code:Id[Long],
             @Required var name: String,
             var artist_id: Long,
             @Required var releaseDate: Date,
             var genre: String,
             var nbVotes: Long = 0L,
             var hasCover: Boolean = false) {

  /*
   * Remove duplicate artist
     * @return found duplicate artist if any exists
     */
  def replaceDuplicateArtist() = {
    val existingArtist = Artist.find("name like {n}").on("n"->filter).first.map( a =>
        //Artist name is unique
	    artist = a
    )  
  }

  /**
   * Vote for an album
   */
  def vote() = {
   nbVotes +=1
   save
  }
}


case class Artist(var code:Id[Long],  @Required var name: String){

}

object Genres {
  def values() = Array("ROCK", "POP", "BLUES", "JAZZ", "HIP-HOP", "WORLD", "OTHER")
}

//Query object for albums
object Album extends Magic[Album] {

  private val formatYear: SimpleDateFormat = new SimpleDateFormat("yyyy")


  /**
   * @param filter
   * @return found albums
   */
  def findAll(filter: String) = {
    //TODO 100 results
    var albums: Seq[Album] = null
    if (filter != null) {
      val likeFilter = "%".concat(filter).concat("%")
      albums = find("name like {n}").on("n"->filter).list()
    }
    else albums = Album.find().list()
    //TODO  albums.sortBy(_.nbVotes).reverse
  }

  /**
   *  first album year
   */                                                                                                                                   A
  def firstAlbumYear: Int = {
    def resultDate = SQL("select min(a.releaseDate) from Album a").apply().head
    if (resultDate != null)
      return formatYear.format(resultDate).toInt
    else return 1990;
  }

  /**
   * last album year
   */
  def lastAlbumYear: Int = {
    def resultDate = SQL("select min(a.releaseDate) from Album a").apply().head
    if (resultDate != null)
      return formatYear.format(resultDate).toInt
    else return formatYear.format(new Date()).toInt
  }

  /**
   * find albums by genre and year
   */
  def findByGenreAndYear(genre: String, year: String) = {
    var albums = find("genre like {g}").on("g"->genre.toUpperCase).list()
    //filter with Scala collections example
    albums = filterByYear(albums, year)
    //another scala example : sort by popularity
    //TODO albums.sortBy(_.nbVotes).reverse
  }

  /**
  * filter by year
  */
  def filterByYear (albums:Seq[Album], year:String) = {
	  albums.filter(x => formatYear.format(x.releaseDate).equals(year))
  }

}

//Query object for artists
object Artist extends Magic[Artist]