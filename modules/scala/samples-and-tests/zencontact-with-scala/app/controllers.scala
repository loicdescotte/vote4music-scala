package controllers

import play._
import play.mvc._
import play.data.validation._

import java.util._

import models._

object Application extends Controller {
    
    def index = {
        Template("now" -> new Date)    
    }
    
    def list = {
        Template("contacts" -> Contacts.find("order by name, firstname").fetch) 
    }
       
    def form(id: Long) = {
        Template("contact" -> Contacts.findById(id).orNull)
    }
    
    def save(@Valid contact: Contact) = {
        if (contact.validateAndSave()) Action(list) else if (request.isAjax) BadRequest else "@form".asTemplate(contact)
    }

}
