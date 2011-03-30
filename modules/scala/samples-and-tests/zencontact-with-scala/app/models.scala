package models

import play.db.jpa._
import play.data.Validators._

import java.util._

// ~~~ Contact

@Entity class Contact(	
    @Required var firstname: String,	
    @Required var name: String,
    @Required var birthdate: Date,
    @Required @Email var email: String
) extends Model

object Contacts extends QueryOn[Contact] 


