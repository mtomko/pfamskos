package org.marktomko.collection

import org.marktomko.util.Closeable

import scala.collection.mutable.Set

class BDBSetIterator(val iterator: BDBMapIterator) extends Iterator[String] with Closeable {
  override def hasNext(): Boolean = {
    iterator.hasNext
  }
  
  override def next(): String = {
    iterator.next()._1
  }
  
  override def close() {
    iterator.close()
  }
}

class BDBSet(val e: BDBEnvironment, val d: String) extends Set[String] with Closeable {
  private val map: BDBMap = new BDBMap(e, d) 
  override def contains(element: String): Boolean = map.get(element) != null
  
  override def += (element: String): BDBSet.this.type = {
    map += element -> ""
    this
  }
  
  override def -= (element: String): BDBSet.this.type = {
    map -= element;
    this
  }
  
  override def iterator(): Iterator[String] = {
    new BDBSetIterator(map.iterator)
  }
  
  override def close() {
    map.close()
  }
}