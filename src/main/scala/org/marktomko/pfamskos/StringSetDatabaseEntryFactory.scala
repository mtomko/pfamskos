package org.marktomko.pfamskos

import org.marktomko.collection.DatabaseEntryFactory

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set

import com.sleepycat.je.DatabaseEntry

class StringSetDatabaseEntryFactory extends DatabaseEntryFactory[Set[String]] {
  val SEPARATOR = 0.toByte
  def toEntry(value: Set[String]): DatabaseEntry = {
    val builder = new ArrayBuffer[Byte]()
    for(v <- value) {
      builder ++= v.getBytes("UTF-8")
      builder += SEPARATOR
    }
    new DatabaseEntry(builder.toArray)
  }
  
  def toValue(entry: DatabaseEntry): Set[String] = {
    val set = new HashSet[String]
    val sb = new ArrayBuffer[Byte]()
    for(b <- entry.getData) {
      if (b == SEPARATOR) {
        set += new String(sb.toArray, "UTF-8")
        sb.clear
      } else {
        sb += b   
      }
    }
    set
  }
  
}