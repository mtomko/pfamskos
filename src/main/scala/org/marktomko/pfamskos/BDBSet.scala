package org.marktomko.pfamskos

import scala.collection.mutable.Set

import com.sleepycat.je.Cursor
import com.sleepycat.je.DatabaseEntry
import com.sleepycat.je.LockMode
import com.sleepycat.je.OperationStatus

class BDBSetIterator(val cursor: Cursor) extends Iterator[String] {
  var open = true
  val foundKey = new DatabaseEntry
  val foundData = new DatabaseEntry

  override def hasNext(): Boolean = {
    if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
      true
    }
    else {
      if (open) {
        close()
      }
      false
    }
  }
  
  override def next(): String = {
    return new String(foundKey.getData, "UTF-8")
  }
  
  def close() {
    cursor.close()
    open = false
  }
}

class BDBSet(val e: BDBEnvironment, val d: String) extends BDBBacked(e, d) with Set[String] {
  override def contains(element: String): Boolean = get(new DatabaseEntry(element.getBytes("UTF-8"))) != null
  
  override def += (element: String): BDBSet.this.type = {
    val key = new DatabaseEntry(element.getBytes("UTF-8"))
    val value = new DatabaseEntry(Array())
    add(key, value)
    this
  }
  
  override def -= (element: String): BDBSet.this.type = {
    remove(new DatabaseEntry(element.getBytes("UTF-8"))); 
    this
  }
  
  override def iterator(): Iterator[String] = {
    new BDBSetIterator(database.openCursor(null, null))
  }
}