package org.marktomko.pfamskos

import com.sleepycat.je.DatabaseEntry

/**
 * Tracks clan membership for proteins using a BDB.
 */
class BDBClanMembershipDatabase(val e: BDBEnvironment, val d: String) extends BDBBacked(e, d, new UTF8StringDatabaseEntryFactory, new UTF8StringDatabaseEntryFactory) with ClanMembershipDatabase  {
  override def addProtein(protein: String, clan: String) {
    add(protein, clan)
  }

  override def clanFor(protein: String): String = {
    get(protein)
  }
}