class FreshDatabase {
    
    import play.test._
    
    import models._

    Fixtures.deleteDatabase()
    
    Yaml[List[Any]]("data.yml").foreach {
                  _ match {
                      case al:Album =>  Album.create(al)
                      case ar:Artist => Artist.create(ar)
                  }
        }

}