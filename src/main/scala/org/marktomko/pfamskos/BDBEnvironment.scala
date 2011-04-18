package org.marktomko.pfamskos

import java.io.File

import com.sleepycat.je.Database
import com.sleepycat.je.DatabaseConfig
import com.sleepycat.je.Environment
import com.sleepycat.je.EnvironmentConfig

class BDBEnvironment(val env: String) {
  val envConfig = new EnvironmentConfig
  envConfig.setAllowCreate(true);
  val environment = new Environment(new File(env), envConfig);
  
  def getDB(db: String, config: DatabaseConfig): Database = environment.openDatabase(null, db, config)
  
  def close() {
    environment.close()
  }
}