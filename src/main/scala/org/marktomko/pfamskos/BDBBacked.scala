package org.marktomko.pfamskos

import java.io.File

import com.sleepycat.je.DatabaseConfig
import com.sleepycat.je.DatabaseEntry
import com.sleepycat.je.Environment
import com.sleepycat.je.EnvironmentConfig
import com.sleepycat.je.LockMode
import com.sleepycat.je.OperationStatus

abstract class BDBBacked(val env: BDBEnvironment, val db: String) {
  val config = new DatabaseConfig
  config.setAllowCreate(true)
  val database = env.getDB(db, config)

  def add(key: DatabaseEntry, value: DatabaseEntry) {
    database.put(null, key, value)
  }
  
  def remove(key: DatabaseEntry) {
    database.delete(null, key) 
  }
  
  def get(key: DatabaseEntry): Array[Byte] = {
    val value = new DatabaseEntry
    if (database.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
      value.getData
    }
    else {
      null
    }
  }
  
  def close() {
    database.close
  }
}