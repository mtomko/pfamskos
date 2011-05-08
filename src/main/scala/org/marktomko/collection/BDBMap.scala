package org.marktomko.collection

import org.marktomko.util.Closeable

import scala.collection.mutable.Map

import com.sleepycat.je.Cursor
import com.sleepycat.je.DatabaseEntry
import com.sleepycat.je.LockMode
import com.sleepycat.je.OperationStatus

class BDBMapIterator(val cursor: Cursor) extends Iterator[Tuple2[String, String]] with Closeable {
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
  
  override def next(): Tuple2[String, String] = {
    return (new String(foundKey.getData, "UTF-8"), new String(foundData.getData, "UTF-8"))
  }
  
  override def close() {
    cursor.close()
    open = false
  }
}

class BDBMap(val e: BDBEnvironment, val d: String) extends BDBBacked[String, String](e, d, new UTF8StringDatabaseEntryFactory, new UTF8StringDatabaseEntryFactory) with Map[String, String] {
  override def += (pair: Tuple2[String,String]): BDBMap.this.type = {
    insert(pair._1, pair._2)
    this
  }
  
  override def -= (element: String): BDBMap.this.type = {
    delete(element);
    this
  }
  
  override def iterator(): BDBMapIterator = {
    new BDBMapIterator(database.openCursor(null, null))
  }
}