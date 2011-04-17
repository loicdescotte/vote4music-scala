import play._;
import play.jobs._;
import play.test._;
 
import models._;
 
@OnApplicationStart
class PopulateOnStart extends Job {

	override def doJob={
		// Check if the database is empty
		if(Albums.count() == 0) {
            Fixtures.load("init-data.yml")
        }
	}
 
}