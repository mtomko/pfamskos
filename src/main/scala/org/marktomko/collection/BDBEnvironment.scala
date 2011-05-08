package org.marktomko.collection

import org.marktomko.util.Closeable

import java.io.File

import com.sleepycat.je.Database
import com.sleepycat.je.DatabaseConfig
import com.sleepycat.je.Environment
import com.sleepycat.je.EnvironmentConfig

/**
 * This class manages a BDB environment that may be shared by a collection of
 * related BDB databases.
 * 
 * @author Mark Tomko, (c) 2011
 */
class BDBEnvironment(val env: String) extends Closeable {
  val envConfig = new EnvironmentConfig
  envConfig.setAllowCreate(true);
  val environment = new Environment(new File(env), envConfig);
  
  /**
   * Opens a database with the given name, using the provided configuration
   */
  def getDatabase(db: String, config: DatabaseConfig): Database = environment.openDatabase(null, db, config)
  
  /**
   * Closes the environment, releasing any allocated resources
   */
  override def close() {
    environment.close()
  }
}