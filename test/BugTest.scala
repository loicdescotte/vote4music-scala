import play.test._
import org.junit._
import org.scalatest.junit._
import org.scalatest._
import org.scalatest.matchers._
import models._
 
 class RelationsTest extends UnitFunSuite with ShouldMatchers {

    Fixtures.deleteAll()
	Fixtures.load("data.yml")

    test("collections") { 
		var albums=Albums.findAll()
	    (albums.size) should be (2)
		
		var artists=Artists.findAll()
		(artists.size) should be (1)
		
		(albums.apply(0).artist) should be (artists.apply(0))
    }
 
}