package play.scalasupport

private[play] object ActionProxy {
    
    def deleguate(controller: String, action: String, args: Any*) {
        val m = play.utils.Java.findActionMethod(action, play.Play.classloader.loadClass(controller))
        try {
            m.invoke(null, args.map(_.asInstanceOf[AnyRef]): _*)
        } catch {
            case t: java.lang.reflect.InvocationTargetException => throw t.getTargetException
        }
    }
}