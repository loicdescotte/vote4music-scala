package models
 
import java.util.{Date,TreeSet,Set=>JSet,List=>JList,ArrayList}
 
//jpa annotations 
import play.db.jpa._
//validations
import play.data.Validators._

@Entity
class Post(

    @Required
    @ManyToOne
    var author: User,
    
    @Required
    var title: String,
    
    @Lob
    @Required
    @MaxSize(10000)
    var content: String

) extends Model {
    
    @Required
    var postedAt = new Date()  
    
    @OneToMany(mappedBy="post", cascade=Array(CascadeType.ALL))
    var comments: JList[Comment] = new ArrayList[Comment]
     
    @ManyToMany(cascade=Array(CascadeType.PERSIST))
    var tags: JSet[Tag] = new TreeSet[Tag]
    
    def addComment(author: String, content: String) = {
        val newComment = new Comment(this, author, content)
        newComment.save()  
        comments.add(newComment)
        this
    }
    
    def previous = Posts.find("postedAt < ? order by postedAt desc", postedAt).first.orNull
    def next = Posts.find("postedAt > ? order by postedAt asc", postedAt).first.orNull
    
    def tagItWith(name: String):this.type = {
        tags add Tags.findOrCreateByName(name)
        this
    }
    
    override def toString() = title
 
}

object Posts extends QueryOn[Post] {
	
    def findTaggedWith(tag: String) = {
        find("select distinct p from Post p join p.tags as t where t.name = ?", tag).fetch
    }
    
    def findTaggedWith(tags: String*) = {
        find("select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size", Map("tags" -> tags.toArray, "size" -> tags.size)).fetch
    }
    
}
