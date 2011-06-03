import play._;
import play.jobs._;
import play.test._;
 
import models._;
 
@OnApplicationStart
class PopulateOnStart extends Job {

	override def doJob={
		// Check if the database is empty
		if(Album.count() == 0) {
            //TODO play-scala bug in relation loading in yml files
			//Fixtures.load("init-data.yml")
        }
	}
 
}