package org.marktomko.pfamskos

import org.marktomko.collection.BDBBacked
import org.marktomko.collection.BDBEnvironment
import org.marktomko.collection.DatabaseEntryFactory
import org.marktomko.collection.UTF8StringDatabaseEntryFactory

/**
 * A multi-map view of membership
 */
class BDBMultiMembershipDatabase(val e: BDBEnvironment, val d: String) extends BDBBacked(e, d, new UTF8StringDatabaseEntryFactory, new StringSetDatabaseEntryFactory) with MultiMembershipDatabase {
  def addMember(member: String, group: String): Unit = {
    val set = get(member).getOrElse(new scala.collection.mutable.HashSet())
    set += group
    insert(member, set)
  }

  def groupsFor(member: String): Set[String] = {
    get(member).getOrElse(new scala.collection.mutable.HashSet[String]()).toSet
  }
}
