package controllers

import play._
import play.mvc._

import models._

object Contacts extends Controller with CRUDFor[Contact] with Secured
object Companies extends Controller with CRUDFor[Company] with Secured