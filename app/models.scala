package models

import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm
import play.data.validation.Required
import java.text.SimpleDateFormat
import java.util.Date


case class Album(
             var id:Id[Long],
             @Required var name: String,
             @Required var releaseDate: Date,
             var genre: String,
             var nbVotes: Long = 0L,
             var hasCover: Boolean = false,
			 var artist_id: Long	
			 ) {

  /*
   * Remove duplicate artist
     * @return found duplicate artist if any exists
     
  def replaceDuplicateArtist() = {
    val existingArtist = Artist.find("name like {n}").on("n"->filter).first.map( a =>
        //Artist name is unique
	    artist = a
    )  
  }*/

  /**
   * Vote for an album
   */
  def vote()={
   this.nbVotes +=1
   Album.update(this)
  }
}


case class Artist(var id:Id[Long],  @Required var name: String){

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
   */                                                                                                                                   
  def firstAlbumYear: Int = {
    
	//val date:Date = SQL("select min(a.releaseDate) as d from Album a").apply.head.asInstanceOf[Date]
	//formatYear.format(date).toInt
	1990
 }

  /**
   * last album year
   */
  def lastAlbumYear: Int = {
	/* 
	val firstRow:Option[Row] = SQL("select min(a.releaseDate) as d from Album a").first()
	 firstRow.map( d =>{
	         formatYear.format(firstRow[Date]("d")).toInt;
 	 }).getOrElse(formatYear.format(new Date()).toInt)
	*/
	formatYear.format(new Date()).toInt
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