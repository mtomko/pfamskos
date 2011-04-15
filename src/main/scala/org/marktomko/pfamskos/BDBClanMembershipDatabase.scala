package org.marktomko.pfamskos

import com.sleepycat.je.DatabaseConfig
import com.sleepycat.je.DatabaseEntry
import com.sleepycat.je.Environment
import com.sleepycat.je.EnvironmentConfig
import com.sleepycat.je.LockMode
import com.sleepycat.je.OperationStatus

import java.io.File

/**
 * Tracks clan membership for proteins using a BDB.
 */
class BDBClanMembershipDatabase(val env: String, val db: String) extends ClanMembershipDatabase {
  val envConfig = new EnvironmentConfig
  envConfig.setAllowCreate(true);
  val environment = new Environment(new File(env), envConfig);
  val dbConfig = new DatabaseConfig
  dbConfig.setAllowCreate(true)
  val database = environment.openDatabase(null, db, dbConfig);

  override def addProtein(protein: String, clan: String) {
    val key = new DatabaseEntry(protein.getBytes("UTF-8"))
    val value = new DatabaseEntry(clan.getBytes("UTF-8"))
    database.put(null, key, value)
  }

  override def clanFor(protein: String): String = {
    val key = new DatabaseEntry(protein.getBytes("UTF-8"))
    val value = new DatabaseEntry
    if (database.get(null, key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
      new String(value.getData, "UTF-8")
    }
    null
  }

  def close() {
    try {
      database.close
    } finally {
      environment.close
    }
  }
}