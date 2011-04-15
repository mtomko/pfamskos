package org.marktomko.pfamskos

import java.io.FileInputStream
import java.io.FileOutputStream

object PfamSkosApp {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("usage: PfamSkosApp <dbenv> <clanfile> <proteinfile> [outputfile]")
      exit(0)
    }

    val dbenv = args(0)
    val clandb = new BDBClanMembershipDatabase(dbenv, "clan_membership")
    try {
      val clanfile = new FileInputStream(args(1))
      val proteinfile = new FileInputStream(args(2))

      val output =
        if (args.length > 3) {
          new FileOutputStream(args(3))
        } else {
          System.out
        }
      val skosWriter = new SkosWriter(output)

      val recordHandler = new CompositeRecordHandler(List(new ProteinClanDatabaseHandler(clandb), new SkosConceptHandler(clandb, skosWriter)))

      StockholmRecordReader.read(clanfile, recordHandler)
      StockholmRecordReader.read(proteinfile, recordHandler)
      
      skosWriter.close()
      
    } finally {
      clandb.close()
    }
  }
}
