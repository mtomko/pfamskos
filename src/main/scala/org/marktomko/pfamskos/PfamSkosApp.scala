package org.marktomko.pfamskos

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

import scala.collection.mutable.Set

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
    val clanMemberDB = new BDBMembershipDatabase(dbenv, "clan_membership")
    val familyMemberDB = new BDBMembershipDatabase(dbenv, "family_membership")
    val familydb = new BDBSet(dbenv, "families")
    val proteindb = new BDBSet(dbenv, "proteins")
    try {
      val clanfile = new FileInputStream(args(1))
      val proteinfile = new FileInputStream(args(2))

      val output =
        if (args.length > 3) {
          new FileOutputStream(args(3))
        } else {
          System.out
        }

      writeSkos(clanMemberDB, familydb, familyMemberDB, proteindb, clanfile, proteinfile, output)
    } finally {
      // attempt to close everything
      try {
        clanMemberDB.close
      } finally {
        try {
          familydb.close
        } finally {
          try {
            proteindb.close
          } finally {
            try {
              familyMemberDB.close
            } finally {
            dbenv.close
            }
          }
        }
      }
    }
  }

  /**
   * Writes the SKOS representation using the provided input streams.
   */
  private def writeSkos(clanMemberDB: MembershipDatabase, familydb: Set[String], familyMemberDB: MembershipDatabase, proteindb: Set[String], clanfile: InputStream, proteinfile: InputStream, output: OutputStream): Unit = {
    val skosWriter = new SkosWriter(output)

    skosWriter.writeConceptScheme(Pfam.PFAM_URL, List(),
      Map((skosWriter.DC, "title") -> "Pfam",
          (skosWriter.DC, "date") -> "2009-07-09",
          (skosWriter.DC, "creator") -> "Sanger Institute"))

    val recordHandler = new CompositeRecordHandler(List(new ProteinDatabaseHandler(clanMemberDB, familydb, familyMemberDB, proteindb), new SkosConceptHandler(clanMemberDB, familydb, skosWriter)))
    
    // process the clans file
    StockholmRecordReader.read(clanfile, recordHandler)
    
    // process the protein families file
    StockholmRecordReader.read(proteinfile, recordHandler)
    
    // now write dummy records for all remaining families
    for(family <- familydb) {
      val clan = clanMemberDB.groupFor(family)
      val broader =
        if (clan == null) {
          List()
        } else {
          List(Pfam.getClanUrl(clan))
        }
      skosWriter.writeConcept(Pfam.getFamilyUrl(family), Pfam.PFAM_URL, "Unknown Protein Family "+family, List(), broader, List(), Map())
    }

    for(protein <- proteindb) {
      val family = familyMemberDB.groupFor(protein)
      val broader = 
        if (family == null) {
          List()
        } else {
          List(Pfam.getFamilyUrl(family))
        }
      skosWriter.writeConcept(Pfam.getProteinUrl(protein), Pfam.PFAM_URL, "Unknown Protein "+protein, List(), broader, List(), Map())
    }
    skosWriter.close()
  }
}
