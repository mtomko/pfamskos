package org.marktomko.pfamskos

import org.marktomko.collection.BDBBacked
import org.marktomko.collection.BDBEnvironment
import org.marktomko.collection.DatabaseEntryFactory
import org.marktomko.collection.UTF8StringDatabaseEntryFactory

/**
 * Tracks clan membership for proteins using a BDB.
 */
class BDBMembershipDatabase(val e: BDBEnvironment, val d: String) extends BDBBacked(e, d, new UTF8StringDatabaseEntryFactory, new UTF8StringDatabaseEntryFactory) with MembershipDatabase {
  override def addMember(member: String, group: String) {
    insert(member, group)
  }

  override def groupFor(member: String): String = {
    get(member).getOrElse(null)
  }
}