package models


import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm
import play.db.anorm.SqlParser._

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

  /**
   * Vote for an album
   */
  def vote()={
   this.nbVotes +=1
   Album.update(this)
  }
}


case class Artist(var id:Id[Long], @Required var name: String){
  def this(name:String) = {
    this(null,name)
  }

}

object Genres {
  def values() = List("ROCK", "POP", "BLUES", "JAZZ", "HIP-HOP", "WORLD", "OTHER")
}

//Query object for albums
object Album extends Magic[Album] {

  private val formatYear: SimpleDateFormat = new SimpleDateFormat("yyyy")

  /**
   * @param filter
   * @return found albums
   */
  def search(filter: String):List[(Album,Artist)] = {
      val likeFilter = "%".concat(filter).concat("%")
      SQL(
       """
           select * from Album al
           join Artist ar on al.artist_id = ar.id
           where al.name like {n}
           or ar.name like {n}
           order by al.nbVotes desc
           limit 100;
       """
      ).on("n"->likeFilter)
      .as( Album ~< Artist ^^ flatten * )
  }

def findAll:List[(Album,Artist)] = 
      SQL(
       """
           select * from Album al
           join Artist ar on al.artist_id = ar.id
           order by al.nbVotes desc
           limit 100;
       """
      ).as( Album ~< Artist ^^ flatten * )


  /**
   *  first album year
   */                                                                                                                                   
  def firstAlbumYear: Int = {	
    SQL("select min(a.releaseDate) from Album a").apply().head match {
      case Row(date:Date) => formatYear.format(date).toInt
      case _ => 1990
    }
  }

  /**
   * last album year
   */
  def lastAlbumYear: Int = {
    SQL("select max(a.releaseDate) from Album a").apply().head match {
      case Row(date:Date) => formatYear.format(date).toInt
      case _ => formatYear.format(new Date()).toInt
    }
  }

  /**
   * find albums by genre and year
   */
  def findByGenreAndYear(genre: String, year: String):List[(Album,Artist)] = {

    val albums = SQL(
       """
           select * from Album al
           join Artist ar on al.artist_id = ar.id
           where al.genre = {g}
           order by al.nbVotes desc
           limit 100;
       """
    ).on("g"->genre.toUpperCase)
    .as( Album ~< Artist ^^ flatten *)
    //filter with Scala collections example
    filterByYear(albums, year)
  }

  /**
  * filter by year
  */
  def filterByYear (albums:List[(Album,Artist)], year:String):List[(Album,Artist)] = {
	  albums.filter(x => formatYear.format(x._1.releaseDate).equals(year))
  }

}

//Query object for artists
object Artist extends Magic[Artist] {

  def findOrCreate(artistName:String):Long= {
      find("name = {n}").on("n"->artistName).first() match{
        case Some(a:Artist) => a.id
        case None => {
           val artist = new Artist(artistName)
           insert(artist)
           //recursive call
           findOrCreate(artist.name)
           //find("name = {n}").on("n"->artistName).single().id
        }
      }
  }

}

