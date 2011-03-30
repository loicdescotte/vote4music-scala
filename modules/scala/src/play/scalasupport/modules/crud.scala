package play.scalasupport.crud

import play.mvc._
import play.scalasupport._
import play.classloading.enhancers.ControllersEnhancer.ByPass

/**
* this trait wraps around the java based CRUD module
*/
trait CRUDWrapper[T] {
    @Before def addType = play.utils.Java.invokeStatic("controllers.CRUD", "addType")    
    @ByPass def index = ActionProxy.deleguate("controllers.CRUD", "index")    
    @ByPass def list(page: Int, search: String, searchFields: String, orderBy: String, order: String) = ActionProxy.deleguate("controllers.CRUD", "list", page, search, searchFields, orderBy, order)
    @ByPass def blank = ActionProxy.deleguate("controllers.CRUD", "blank")
    @ByPass def save(id: String) = ActionProxy.deleguate("controllers.CRUD", "save", id)
    @ByPass def create = ActionProxy.deleguate("controllers.CRUD", "create")
    @ByPass def delete(id: String) = ActionProxy.deleguate("controllers.CRUD", "delete", id)
    @ByPass def show(id: String) = ActionProxy.deleguate("controllers.CRUD", "show", id)
    @ByPass def attachment(id: String, field: String) = ActionProxy.deleguate("controllers.CRUD", "attachment", id, field)
    
}

