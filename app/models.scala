package controllers

import java.util._
import javax.persistence._
import play.db.jpa.Model
import play.data.validation.Required
import javax.persistence.{EnumType, TemporalType, CascadeType}

class Album(
             @Required name: String,
             @ManyToOne(cascade = { CascadeType.PERSIST}) artist: Artist,
             @Temporal(TemporalType.DATE) @Required releaseDate: Date,
             @Enumerated(EnumType.STRING) genre : Genre,
             var nbVotes: Long = OL,
             var hasCove: Boolean = false)
  extends Model {

  /*
   * Remove duplicate artist
     * @return found duplicate artist if exists
     */
  def replaceDuplicateArtist = {
    def existingArtists: Artist = Artists.findByName(artist.name)
    if (existingArtists.size() > 0) {
      //Artist name is unique
      artist = existingArtists.get(0)
    }
  }
}

class Artist(
@Required @Column(unique = true) name:String) extends Model{
  def findByName(name:String) = {
        find("byName", name).fetch();
    }
}

sealed trait Genre
final case object ROCK extends Genre
final case object METAL extends Genre
final case object POP extends Genre

object Albums extends QueryOn[Album]
object Artists extends QueryOn[Artist]