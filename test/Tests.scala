import java.util.Calendar
import play.test._
 
import org.scalatest._
import org.scalatest.matchers._
import models._

class CoreTest extends UnitFlatSpec with ShouldMatchers {

    it should "return right albums with years" in {

      val artist = new Artist("joe")

      val c1 = Calendar.getInstance()
      c1.set(2010,1,1)
      val album = new Album("album1", c1.getTime, "ROCK", false)

      val c2 = Calendar.getInstance()
      c2.set(2009,1,1)
      val album2 = new Album("album2", c2.getTime, "ROCK", false)

      val albums = List(new Tuple2(album,artist),new Tuple2(album2,artist))

      val filteredAlbums = Album.filterByYear(albums, "2010")
      filteredAlbums.size should be (1)
    }


}
 
class ApplicationTest extends UnitFlatSpec with ShouldMatchers with BeforeAndAfterEach {

    override def beforeEach() {
        Fixtures.deleteDatabase()
    }

    it should "return right albums with years" in {
      val c1 = Calendar.getInstance()
      c1.set(2010,1,1)
      val album = new Album("album1", c1.getTime, "ROCK", false)
      Album.create(album)

      val c2 = Calendar.getInstance()
      c2.set(2009,1,1)
      val album2 = new Album("album2", c2.getTime, "ROCK", false)
      Album.create(album2)

      val albums = Album.findAll
      //TODO database gives no result
      //albums.size should be (2)

      val filteredAlbums = Album.findByGenreAndYear("ROCK", "2010")
      //filteredAlbums.size should be (1)
    }


}