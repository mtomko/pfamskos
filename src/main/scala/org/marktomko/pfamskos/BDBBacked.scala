package org.marktomko.pfamskos

import com.sleepycat.je.DatabaseConfig
import com.sleepycat.je.DatabaseEntry
import com.sleepycat.je.LockMode
import com.sleepycat.je.OperationStatus

/**
 * This abstract class may be used as a starting point for implementing any
 * BDB-backed store.
 * 
 * @author Mark Tomko, (c) 2011
 */
abstract class BDBBacked[K, V >: Null <: AnyRef](val env: BDBEnvironment, val db: String, val keyFactory: DatabaseEntryFactory[K], val valueFactory: DatabaseEntryFactory[V]) {
  val config = new DatabaseConfig
  config.setAllowCreate(true)
  val database = env.getDatabase(db, config)

  /**
   * Adds the key/value pair to the database
   * @param key
   * @param value
   * @return Unit
   */
  def insert(key: K, value: V) {
    database.put(null, keyFactory.toEntry(key), valueFactory.toEntry(value))
  }
  
  /**
   * Removes the value corresponding to the key from the database
   * @param key
   * @return Unit
   */
  def delete(key: K) {
    database.delete(null, keyFactory.toEntry(key)) 
  }
  
  /**
   * Retrieves the value corresponding to the given key
   */
  def get(key: K): Option[V] = {
    val value = new DatabaseEntry
    if (database.get(null, keyFactory.toEntry(key), value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
      new Some(valueFactory.toValue(value))
    }
    else {
      None
    }
  }
  
  /**
   * Closes the database and frees any allocated resources
   */
  def close() {
    database.close
  }
}