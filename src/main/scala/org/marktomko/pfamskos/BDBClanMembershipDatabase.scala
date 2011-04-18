package org.marktomko.pfamskos

import com.sleepycat.je.DatabaseEntry

/**
 * Tracks clan membership for proteins using a BDB.
 */
class BDBClanMembershipDatabase(val e: BDBEnvironment, val d: String) extends BDBBacked(e, d) with ClanMembershipDatabase  {
  override def addProtein(protein: String, clan: String) {
    val key = new DatabaseEntry(protein.getBytes("UTF-8"))
    val value = new DatabaseEntry(clan.getBytes("UTF-8"))
    add(key, value)
  }

  override def clanFor(protein: String): String = {
    val key = new DatabaseEntry(protein.getBytes("UTF-8"))
    val value = get(key)
    if (value != null) {
      new String(value, "UTF-8")
    }
    else {
      null
    }
  }
}