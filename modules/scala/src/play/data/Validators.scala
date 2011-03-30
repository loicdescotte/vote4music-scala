package play.data


import annotation.target.field

/**
 * provides aliases for play's validations
 */

trait Validators {
  type CheckWith = validation.CheckWith  @field
  type Email = validation.Email @field
  type Equals = validation.Equals @field
  type InFuture = validation.InFuture @field
  type InPast = validation.InPast
  type IsTrue = validation.IsTrue @field
  type Match = validation.Match  @field
  type Max = validation.Max @field
  type MaxSize = validation.MaxSize @field
  type Min = validation.Min @field
  type MinSize = validation.MinSize @field
  type Password = validation.Password @field
  type Range = validation.Range @field
  type Required = validation.Required @field 
  type URL = validation.URL @field
  type Valid = validation.Valid @field
}

object Validators extends Validators

// vim: set ts=4 sw=4 et:
