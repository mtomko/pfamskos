package org.marktomko.pfamskos

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