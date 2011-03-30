package play_with_scala {

class Scrapbook {
    print( new User("Guillaumexxx").sayYourName(10) )
    
    val r = 1 to 10
    print(r)
    print( r.filter( _ % 2 == 0) )
    print( r.map( "Number: " + _ ) )
    print( r.filter( _ % 2 == 0).map( "Number " + _ ) )
    print( r.reduceLeft( _ * _ ) )
    
}

class User(val name:String) {
    
    def sayYourName(times: Int) = "I'm " + (for(i <- 0 until times) yield name).reduceLeft( _ + ", " + _ )
    
}

}
