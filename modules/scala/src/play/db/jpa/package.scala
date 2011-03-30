/**
* overriding Model with Scala version
*/
import play.data.validation._
import javax.persistence

package play { 
    
  package db {
  import annotation.target.field

  package object jpa{
      //enums
      val CascadeType = CascadeTypeWrapper
      val LockModeType = LockModeTypeWrapper
      val FetchType = FetchTypeWrapper
      //classes
      type Table = persistence.Table
      type Entity = persistence.Entity
      type Inheritance = persistence.Inheritance

      //javax.persistence field
      type  AttributeOverrides = persistence.AttributeOverrides @field
      type  Basic = persistence.Basic  @field
      type  Column = persistence.Column @field
      type  ColumnResult = persistence.ColumnResult  @field
      type  Embedded = persistence.Embedded  @field
      type  EmbeddedId = persistence.EmbeddedId  @field
      type  EntityResult = persistence.EntityResult @field
      type  Enumerated = persistence.Enumerated  @field
      type  ExcludeDefaultListeners = persistence.ExcludeDefaultListeners  @field
      type  ExcludeSuperclassListeners = persistence.ExcludeSuperclassListeners  @field
      type  FieldResult = persistence.FieldResult  @field
      type  GeneratedValue = persistence.GeneratedValue  @field
      type  Id = persistence.Id  @field
      type  IdClass = persistence.IdClass  @field
      type  JoinColumn = persistence.JoinColumn  @field
      type  JoinColumns = persistence.JoinColumns  @field
      type  JoinTable = persistence.JoinTable  @field
      type  Lob = persistence.Lob @field
      type  ManyToMany = persistence.ManyToMany  @field
      type  ManyToOne = persistence.ManyToOne  @field
      type  MapKey = persistence.MapKey  @field
      type  OneToMany = persistence.OneToMany  @field
      type  OneToOne = persistence.OneToOne  @field
      type  OrderBy = persistence.OrderBy  @field
      type  PostLoad = persistence.PostLoad  @field
      type  PostPersist = persistence.PostPersist  @field
      type  PostRemove = persistence.PostRemove  @field
      type  PostUpdate = persistence.PostUpdate  @field
      type  PrePersist = persistence.PrePersist  @field
      type  PreRemove = persistence.PreRemove   @field
      type  PreUpdate = persistence.PreUpdate   @field
      type  QueryHint = persistence.QueryHint  @field
      type  SequenceGenerator = persistence.SequenceGenerator  @field
      type  TableGenerator = persistence.TableGenerator  @field
      type  Temporal = persistence.Temporal  @field
      type  Transient = persistence.Transient  @field
      type  UniqueConstraint = persistence.UniqueConstraint  @field
      type  Version = persistence.Version  @field


      //model
      type Model = play.db.jpa.ScalaModel
        
    }
      
  }
    
}
