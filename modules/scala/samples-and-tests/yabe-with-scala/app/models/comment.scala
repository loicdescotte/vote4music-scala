package models
 
import java.util._
 
import play.db.jpa._
import play.data.Validators._

@Entity
class Comment (
       
       @ManyToOne
       @Required
       var post: Post,
       
       @Required
       var author: String, 
       
       @Lob
       @Required
       @MaxSize(10000)
       var content: String

) extends Model {
    
    @Required
    var postedAt = new Date()
    
    override def toString() = {
        if(content.length() > 50) content.substring(0, 50) else content
    }
 
}

object Comments extends QueryOn[Comment]
