import play._;
import play.jobs._;
import play.test._;
 
import models._;
 
@OnApplicationStart
class PopulateOnStart extends Job {

	override def doJob={
		// Check if the database is empty
		if(Album.count().single() == 0) {
		            Yaml[List[Any]]("data.yml").foreach {
			                _ match {
			                    case al:Album =>  Album.create(al)
			                    case ar:Artist => Artist.create(ar)
			                }
			      }
			}
	}
 
}