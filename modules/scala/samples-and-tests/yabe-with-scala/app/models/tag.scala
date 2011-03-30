package models
 
import java.util._
 
import play.db.jpa._
import play.data.Validators._

@Entity
class Tag (@Required var name:String) extends Model with Comparable[Tag] {
    
    override def toString() = name    
    override def compareTo(otherTag: Tag) = name.compareTo(otherTag.name)
 
}

object Tags extends QueryOn[Tag]{
    
    def allTags = Tags.findAll
    
    def findOrCreateByName(name: String) = {
        find("byName", name).first match {
            case Some(t) => t
            case None => new Tag(name).save()
        }
    }
    
    def cloud = {
        find("select new map(t.name as tag, count(p.id) as pound) from Post p join p.tags as t group by t.name").fetch
    }
    
}
