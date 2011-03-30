import java.util._
import play.test._
import models._

import scala.collection.JavaConversions._

import org.scalatest.{FlatSpec,BeforeAndAfterEach}
import org.scalatest.matchers.ShouldMatchers
import scala.collection.mutable.Stack

class BasicTest extends UnitFlatSpec with ShouldMatchers with BeforeAndAfterEach {
    
    override def beforeEach() {
        Fixtures.deleteAll()
    }
 
    it should "create and retrieve a user" in {
        // Create a new user and save it
        new User("bob@gmail.com", "secret", "Bob").save()

        // Retrieve the user with bob username
        var bob = Users.find("byEmail", "bob@gmail.com").first

        // Test 
        bob should not be (None)
        "Bob" should equal (bob.get.fullname)
    }
    
    
    it should "call connect on User" in {
        // Create a new user and save it
        new User("bob@gmail.com", "secret", "Bob").save()

        // Test 
        Users.connect("bob@gmail.com", "secret") should not be (None)
        Users.connect("bob@gmail.com", "badpassword") should be (None)
        Users.connect("tom@gmail.com", "secret") should be (None)
    }
    
    it should "create Post" in {
        // Create a new user and save it
        var bob = new User("bob@gmail.com", "secret", "Bob").save()

        // Create a new post
        new Post(bob, "My first post", "Hello world").save()

        // Test that the post has been created
        1 should equal (Posts.count())

        // Retrieve all post created by bob
        var bobPosts = Posts.find("byAuthor", bob).fetch

        // Tests
        1 should equal (bobPosts.size)
        var firstPost = bobPosts(0)
        firstPost should not be (null)
        bob should equal (firstPost.author)
        "My first post" should equal ( firstPost.title)
        "Hello world" should equal ( firstPost.content)
        firstPost.postedAt should not be (null)
    }
    
    it should "post Comments" in {
        // Create a new user and save it
        var bob = new User("bob@gmail.com", "secret", "Bob").save()

        // Create a new post
        var bobPost = new Post(bob, "My first post", "Hello world").save()

        // Post a first comment
        new Comment(bobPost, "Jeff", "Nice post").save()
        new Comment(bobPost, "Tom", "I knew that !").save()

        // Retrieve all comments
        var bobPostComments = Comments.find("byPost", bobPost).fetch

        // Tests
        2 should equal (bobPostComments.size)

        var firstComment = bobPostComments(0)
        firstComment should not be (null)
        "Jeff" should equal ( firstComment.author)
        "Nice post" should equal ( firstComment.content)
        firstComment.postedAt should not be (null)

        var secondComment = bobPostComments(1)
        secondComment should not be (null)
        "Tom" should equal ( secondComment.author)
        "I knew that !" should equal ( secondComment.content)
        secondComment.postedAt should not be (null)
    }
    
    it should "use the comments relation" in {
        // Create a new user and save it
        var bob = new User("bob@gmail.com", "secret", "Bob").save()

        // Create a new post
        var bobPost = new Post(bob, "My first post", "Hello world").save()

        // Post a first comment
        bobPost.addComment("Jeff", "Nice post")
        bobPost.addComment("Tom", "I knew that !")

        // Count things
        1 should equal (Users.count())
        1 should equal (Posts.count())
        2 should equal (Comments.count())

        // Retrieve the bob post
        val getBobPost = Posts.find("byAuthor", bob).first
        getBobPost should not be (None)

        // Navigate to comments
        2  should equal (getBobPost.get.comments.size)
        "Jeff" should equal (getBobPost.get.comments(0).author)

        // Delete the post
        getBobPost.get.delete()

        // Chech the all comments have been deleted
        1 should equal (Users.count())
        0 should equal (Posts.count())
        0 should equal (Comments.count())
    }
    
    it should "work if things combined together" in {
        Fixtures.load("data.yml")

        // Count things
        2 should equal (Users.count())
        3 should equal (Posts.count())
        3 should equal (Comments.count())

        // Try to connect as users
        Users.connect("bob@gmail.com", "secret") should not be (None)
        Users.connect("jeff@gmail.com", "secret") should not be (None)
        Users.connect("jeff@gmail.com", "badpassword") should be (None)
        Users.connect("tom@gmail.com", "secret") should be (None)

        // Find all bob posts
        var bobPosts = Posts.find("author.email", "bob@gmail.com").fetch
        2 should equal (bobPosts.size)

        // Find all comments related to bob posts
        var bobComments = Comments.find("post.author.email", "bob@gmail.com").fetch
        3 should equal (bobComments.size)

        // Find the most recent post
        var frontPost = Posts.find("order by postedAt desc").first

        frontPost should not be (None)
        "About the model layer" should equal (frontPost.get.title)

        // Check that this post has two comments
        2 should equal (frontPost.get.comments.size)

        // Post a new comment
        frontPost.get.addComment("Jim", "Hello guys")
        3 should equal (frontPost.get.comments.size)
        4 should equal (Comments.count())
    }
    
    it should "be able to handle Tags" in {
        // Create a new user and save it
        var bob = new User("bob@gmail.com", "secret", "Bob").save()
        
        // Create a new post
        var bobPost = new Post(bob, "My first post", "Hello world").save()
        var anotherBobPost = new Post(bob, "My second post post", "Hello world").save()
        
        // Well
        0 should equal (Posts.findTaggedWith("Red").size)
        
        // Tag it now
        bobPost.tagItWith("Red").tagItWith("Blue").save()
        anotherBobPost.tagItWith("Red").tagItWith("Green").save()
        
        Tags.findAll.size should equal (3)

        // Check
        2 should equal (Posts.findTaggedWith("Red").size)        
        1 should equal (Posts.findTaggedWith("Blue").size)
        1 should equal (Posts.findTaggedWith("Green").size)
        
        1 should equal (Posts.findTaggedWith("Red", "Blue").size)   
        1 should equal (Posts.findTaggedWith("Red", "Green").size)   
        0 should equal (Posts.findTaggedWith("Red", "Green", "Blue").size)  
        0 should equal (Posts.findTaggedWith("Green", "Blue").size)    
        
        var cloud = Tags.cloud
        "List({tag=Red, pound=2}, {tag=Blue, pound=1}, {tag=Green, pound=1})" should equal (cloud.toString)
        
    }
 
}
