package models

import java.util._
import play.db.jpa._
import play.data.validation.Required
import javax.persistence.{EnumType, TemporalType, CascadeType}

class Album(
             @Required name: String,
             @ManyToOne/*(cascade = {
               CascadeType.PERSIST
             })*/ var artist: Artist,
             @Temporal(TemporalType.DATE) @Required var releaseDate: Date,
             @Enumerated(EnumType.STRING) var genre: Genre,
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



trait Genre /* {

  final case object ROCK extends Genre

  final case object METAL extends Genre

  final case object POP extends Genre

}                    */

object Albums extends QueryOn[Album]

object Artists extends QueryOn[Artist]