package org.marktomko.collection

import com.sleepycat.je.DatabaseEntry

/**
 * Factory trait for converting types to and from DatabaseEntry
 */
trait DatabaseEntryFactory[A] {
    def toEntry(value: A): DatabaseEntry
    def toValue(entry: DatabaseEntry): A
}