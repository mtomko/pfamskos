package org.marktomko.pfamskos

import org.marktomko.collection.BDBEnvironment
import org.marktomko.collection.BDBMap
import org.marktomko.collection.BDBSet
import org.marktomko.pfamskos.stockholm.CompositeRecordHandler
import org.marktomko.pfamskos.stockholm.StockholmRecordReader
import org.marktomko.pfamskos.uniprot.UniprotNameHandler
import org.marktomko.pfamskos.uniprot.UniprotReader

import org.marktomko.util.Closer

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
      println("usage: PfamSkosApp <dbenv> <clanfile> <proteinfile> <uniprotfile> [outputfile]")
      exit(0)
    }

    val dbenv = new BDBEnvironment(args(0))
    val clanMemberDB = new BDBMembershipDatabase(dbenv, "clan_membership")
    val familyMemberDB = new BDBMultiMembershipDatabase(dbenv, "family_membership")
    val familydb = new BDBSet(dbenv, "families")
    val proteindb = new BDBSet(dbenv, "proteins")
    val uniprotdb = new BDBMap(dbenv, "uniprot")
    try {
      val clanfile = inputStreamFor(args(1))
      val proteinfile = inputStreamFor(args(2))
      val uniprotfile = inputStreamFor(args(3))

      val output =
        if (args.length > 4) {
          new FileOutputStream(args(4))
        } else {
          System.out
        }

      writeSkos(clanMemberDB, familydb, familyMemberDB, proteindb, uniprotdb, clanfile, proteinfile, uniprotfile, output)
    } finally {
      // attempt to close everything
      Closer.tryClose(List(clanMemberDB, familydb, proteindb, familyMemberDB, uniprotdb, dbenv))
    }
  }

  /**
   * Writes the SKOS representation using the provided input streams.
   */
  private def writeSkos(clanMemberDB: MembershipDatabase, familydb: Set[String], familyMemberDB: MultiMembershipDatabase, proteindb: Set[String], uniprotdb: scala.collection.mutable.Map[String, String], clanfile: InputStream, proteinfile: InputStream, uniprotfile: InputStream, output: OutputStream): Unit = {
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
    
    // process the uniprot file
    val uniprotHandler = new UniprotNameHandler(uniprotdb)
    UniprotReader.read(uniprotfile, uniprotHandler)
    
    // now write dummy records for all remaining families
    for(family <- familydb) {
      val clan = clanMemberDB.groupFor(family)
      val broader =
        if (clan == null) {
          List()
        } else {
          List(Pfam.getClanUrl(clan))
        }
      skosWriter.writeConcept(Pfam.getFamilyUrl(family), Pfam.PFAM_URL, "Unknown Protein Family "+family, List(), broader, List(), Map(), Map())
    }

    //TODO: make this optional based on command-line arguments
    /*
    for(protein <- proteindb) {
      val families = familyMemberDB.groupsFor(protein)
      val broader: Iterable[String] = 
        if (families == null) {
          List()
        } else {
          families.map(Pfam.getFamilyUrl(_))
        }

      val prefLabel = uniprotdb.get(protein).getOrElse("Unknown Protein " + protein)

      skosWriter.writeConcept(Pfam.getProteinUrl(protein), Pfam.PFAM_URL, prefLabel, List(), broader, List(), Map(), Map())
    }
    */

    skosWriter.close()
  }
  
  private def inputStreamFor(filename: String): InputStream = {
    if (filename.toLowerCase endsWith ".gz") {
      new java.util.zip.GZIPInputStream(new FileInputStream(filename))
    } else {
      new FileInputStream(filename)
    }
  }
}
