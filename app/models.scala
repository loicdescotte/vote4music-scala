package models

import java.util._
import javax.persistence._
import play.db.jpa.Model
import play.db.jpa.QueryOn
import play.data.validation.Required
import javax.persistence.{EnumType, TemporalType, CascadeType}


class Album(
             @Required name: String,
             @ManyToOne(cascade = Array(CascadeType.PERSIST, CascadeType.MERGE))
             var artist: Artist,
             @Temporal(TemporalType.DATE) @Required var releaseDate: Date,
             var genre: String,
             var nbVotes: Long = 0L,
             var hasCove: Boolean = false)
  extends Model {

  /*
   * Remove duplicate artist
     * @return found duplicate artist if exists
     */
  def replaceDuplicateArtist() = {
    val existingArtists = Artists.find("byName",artist.name).fetch()
    if (existingArtists.size > 0)
    //Artist name is unique
      artist = existingArtists.apply(0)
  }

}

class Artist(
  @Required @Column(unique = true) var name: String) extends Model {

}


object Genres {
  def values() = Array("ROCK","POP","BLUES","JAZZ", "HIP-HOP", "WORLD","OTHER")
}


object Albums extends QueryOn[Album]

object Artists extends QueryOn[Artist]