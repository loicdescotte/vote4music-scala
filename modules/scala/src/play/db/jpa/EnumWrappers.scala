package play.db.jpa
import javax.persistence

/**
 * These wrappers are needed because type alias does not work for java enums
 */
private [jpa] object CascadeTypeWrapper {
 final val ALL = persistence.CascadeType.ALL
 final val MERGE = persistence.CascadeType.MERGE
 final val PERSIST = persistence.CascadeType.PERSIST
 final val REFRESH = persistence.CascadeType.REFRESH
 final val REMOVE  = persistence.CascadeType.REMOVE 
}


private [jpa] object LockModeTypeWrapper {
  final val READ = persistence.LockModeType.READ
  final val WRITE = persistence.LockModeType.WRITE 
  final val OPTIMISTIC = persistence.LockModeType.OPTIMISTIC
  final val OPTIMISTIC_FORCE_INCREMENT = persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT
  final val PESSIMISTIC_READ = persistence.LockModeType.PESSIMISTIC_READ
  final val PESSIMISTIC_WRITE = persistence.LockModeType.PESSIMISTIC_WRITE
  final val PESSIMISTIC_FORCE_INCREMENT = persistence.LockModeType.PESSIMISTIC_FORCE_INCREMENT
  final val NONE = persistence.LockModeType.NONE
}

private[jpa] object FetchTypeWrapper {
  final val EAGER = persistence.FetchType.EAGER
  final val LAZY = persistence.FetchType.LAZY
}

