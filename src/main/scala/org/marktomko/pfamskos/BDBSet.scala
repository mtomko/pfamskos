package org.marktomko.pfamskos

import scala.collection.mutable.Set

class BDBSetIterator(val iterator: BDBMapIterator) extends Iterator[String] {
  override def hasNext(): Boolean = {
    iterator.hasNext
  }
  
  override def next(): String = {
    iterator.next()._1
  }
  
  def close() {
    iterator.close()
  }
}

class BDBSet(val e: BDBEnvironment, val d: String) extends Set[String] {
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
  
  def close() {
    map.close()
  }
}