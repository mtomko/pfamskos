package org.marktomko.pfamskos

import scala.collection.mutable.Set

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * This application encodes the logic used to transform the Pfam files into a
 * SKOS representation.
 * 
 * @author Mark Tomko, (c) 2011
 */
object PfamSkosApp {
  /**
   * The main application entry point; parses arguments and dispatches actions
   * to the writeSkos() method.
   */
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("usage: PfamSkosApp <dbenv> <clanfile> <proteinfile> [outputfile]")
      exit(0)
    }

    val dbenv = new BDBEnvironment(args(0))
    val clandb = new BDBClanMembershipDatabase(dbenv, "clan_membership")
    val clans = new BDBSet(dbenv, "clans")
    val clanless = new BDBSet(dbenv, "clanless")
    try {
      val clanfile = new FileInputStream(args(1))
      val proteinfile = new FileInputStream(args(2))

      val output =
        if (args.length > 3) {
          new FileOutputStream(args(3))
        } else {
          System.out
        }

      writeSkos(clandb, clans, clanless, clanfile, proteinfile, output)
    } finally {
      // attempt to close everything
      try {
        try {
          clandb.close
        } finally {
          try { 
            clans.close
          } finally {
            clanless.close
          }
        }
      }
      finally {
        dbenv.close
      }
    }
  }

  /**
   * Writes the SKOS representation using the provided input streams.
   */
  private def writeSkos(clandb: ClanMembershipDatabase, clans: Set[String], clanless: Set[String], clanfile: InputStream, proteinfile: InputStream, output: OutputStream): Unit = {
    val skosWriter = new SkosWriter(output)

    //skosWriter.writeConceptScheme(Pfam.PFAM_URL, PfamSkos.TOP_CONCEPTS,
    skosWriter.writeConceptScheme(Pfam.PFAM_URL, List(),
      Map((skosWriter.DC, "title") -> "Pfam",
          (skosWriter.DC, "date") -> "2009-07-09",
          (skosWriter.DC, "creator") -> "Sanger Institute"))

    val recordHandler = new CompositeRecordHandler(List(new ProteinClanDatabaseHandler(clandb), new SkosConceptHandler(clandb, clans, clanless, skosWriter)))
    
    // process the clans file
    StockholmRecordReader.read(clanfile, recordHandler)
    
    // write the top-level concept for clans
    val clanURL = Pfam.PFAM_URL + "/clans/browse"
    //skosWriter.writeConcept(clanURL, Pfam.PFAM_URL, "Clan", List(), List(), clans, Map()) 
    
    // process the protein families file
    StockholmRecordReader.read(proteinfile, recordHandler)
    
    // write the clanless "superclan"
    //skosWriter.writeConcept(PfamSkos.NULL_CLAN, Pfam.PFAM_URL, "Clanless", List(), List(), clanless, Map())

    skosWriter.close()
  }
}
