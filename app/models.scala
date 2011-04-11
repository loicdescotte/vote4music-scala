package models

import _root_.models.Artists
import java.util._
import javax.persistence._
import play.db.jpa.Model
import play.db.jpa.QueryOn
import play.db.jpa.JPA
import play.data.validation.Required
import javax.persistence.{EnumType, TemporalType, CascadeType}
import java.text.SimpleDateFormat


@Entity
class Album(
             @Required name: String,
             @ManyToOne(cascade = Array(CascadeType.PERSIST, CascadeType.MERGE))
             var artist: Artist,
             @Temporal(TemporalType.DATE) @Required var releaseDate: Date,
             var genre: String,
             var nbVotes: Long = 0L,
             var hasCover: Boolean = false)
  extends Model {

  /*
   * Remove duplicate artist
     * @return found duplicate artist if any exists
     */
  def replaceDuplicateArtist() = {
    val existingArtists = Artists.find("byName", artist.name).fetch()
    if (existingArtists.size > 0)
    //Artist name is unique
    artist = existingArtists.apply(0)
  }

}

@Entity
class Artist(@Required @Column(unique = true) var name: String) extends Model {

}


object Genres {
  def values() = Array("ROCK", "POP", "BLUES", "JAZZ", "HIP-HOP", "WORLD", "OTHER")
}

//Query object for albums
object Albums extends QueryOn[Album] {

  private val formatYear: SimpleDateFormat = new SimpleDateFormat("yyyy");

  def em() = JPA.em()

  def firstAlbumYear(): Int = {
    def resultDate = em().createQuery("select min(a.releaseDate) from Album a").getSingleResult().asInstanceOf[Date]
    if (resultDate != null)
      return formatYear.format(resultDate).toInt
    else return 1990;
  }

  def lastAlbumYear(): Int = {
    def resultDate = em().createQuery("select min(a.releaseDate) from Album a").getSingleResult().asInstanceOf[Date]
    if (resultDate != null)
      return formatYear.format(resultDate).toInt
    else return formatYear.format(new Date()).toInt
  }

  def findByGenreAndYear(genre: String, year: String) {
    var albums = Albums.find("byGenre", genre.toUpperCase()).fetch()
    //filter with Scala collections example
    albums = albums.filter(x => formatYear.format(x.releaseDate).equals(year))
    //sort by popularity
    albums.sortBy(_.nbVotes).reverse
  }
}

//Query object for artists
object Artists extends QueryOn[Artist]