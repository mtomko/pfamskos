package org.marktomko.pfamskos

import com.sleepycat.je.DatabaseEntry

/**
 * Factory trait for converting types to and from DatabaseEntry
 */
trait DatabaseEntryFactory[A] {
    def toEntry(value: A): DatabaseEntry
    def toValue(entry: DatabaseEntry): A
}

/**
 * Converts UTF-8 Strings to and from DatabaseEntry instances
 */
class UTF8StringDatabaseEntryFactory extends DatabaseEntryFactory[String] {
  override def toEntry(value: String): DatabaseEntry = {
    new DatabaseEntry(value.getBytes("UTF-8"))
  }
  override def toValue(entry: DatabaseEntry): String = {
    new String(entry.getData, "UTF-8")
  }
}