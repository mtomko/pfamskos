package org.marktomko.pfamskos.stockholm

import org.marktomko.pfamskos.SubstitutionStringTransform

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import scala.io.Source

import java.io.InputStream

/**
 * This object reads records from a file containing records in the
 * Stockholm 1.0 format. For more information on this file format, see
 * http://en.wikipedia.org/wiki/Stockholm_format
 *
 * Each record begins with the line:
 * # STOCKHOLM 1.0
 *
 * Records end with the line
 * //
 *
 * All remaining lines match one of the following patterns:
 * #=GF <feature> <Generic per-File annotation, free text>
 * #=GC <feature> <Generic per-Column annotation, exactly 1 char per column>
 * #=GS <seqname> <feature> <Generic per-Sequence annotation, free text>
 * #=GR <seqname> <feature> <Generic per-Sequence AND per-Column markup, exactly 1 char per column>
 *
 * @author Mark Tomko, (c) 2011
 */
object StockholmRecordReader {
  val START = "# STOCKHOLM 1.0"
  val END = "//"
  val ID = "ID"
  val FIELD_PREFIX = "#"

  // line prefixes
  val GF = "GF"
  val GS = "GS"
  val GC = "GC"
  val GR = "GR"

  // line types
  val AC = "AC" // accession
  val CC = "CC" // comment
  val CL = "CL" // clan accession
  val DE = "DE" // description
  val MB = "MB" // member protein family
  val RM = "RM" // medline reference number
  val TP = "TP" // type

  // this matches any line that is not a start, end, or part of an alignment
  // the capturing groups are the prefix, and the rest of the line, which may 
  // be split further by subsequent regex
  val NON_ALIGNMENT_LINE = """#=(G[FSRC]) ([^\n]+)""".r

  // this splits a #=GF line into a field and value 
  val GF_ANNOTATION = """([A-Z]{2})[ ]+([^\n]+)""".r
  
  val MB_PROTEIN_FAM = """([A-Z0-9\.]+);""".r

  val UNIPROT_PROTEIN_AC = """([A-Z0-9]+)\.[^\n]+""".r

  // this splits a #=GS line into a protein, subfield, and subfield value
  val GS_ANNOTATION = """([A-Z\d-_/]+)[ ]+([A-Z]{2})[ ]*([^\n]+)""".r

  val nullCharTransform = new SubstitutionStringTransform(new String(Array(0.toChar)), "")
  
  /**
   * Reads in a Stockholm file, building a representation in main memory.
   * @param file The name of the Stockholm file to read
   * @return A list of [[StockholmRecord]]
   */
  def read(stream: InputStream, handler: StockholmRecordHandler) {
    // these comprise mutable state representing a record that's being read
    var id: String = null
    var map: Map[String, ListBuffer[String]] = null
    var memberFamilies: ListBuffer[String] = null
    var memberProteins: ListBuffer[String] = null
    
    for (line <- Source.fromInputStream(stream, "UTF-8").getLines) {
      if (line == START) {
        // we've reached the start of a new record, so initialize a new record
        map = new HashMap[String, ListBuffer[String]]
        memberFamilies = new ListBuffer[String]
        memberProteins = new ListBuffer[String]
      } else if (line == END) {
        // we've read the end of a record, so apply the handler
        handler(new StockholmRecord(id, map, memberFamilies.toList, memberProteins.toList))
      } else if (line startsWith FIELD_PREFIX) {
        // we're reading lines comprising a record, so parse them
        val NON_ALIGNMENT_LINE(prefix, rest) = line
        prefix match {
          case GF =>
            // GF lines represent a basic field; if it's an interesting one, store it
            val GF_ANNOTATION(field, value) = rest
            if (field == ID) {
              // ID gets special treatment
              id = value
            } else if (field == MB) {
              val MB_PROTEIN_FAM(family) = value
              memberFamilies += family
            } else {
              // everything else just gets stored
              val values =
                if (map.contains(field)) map(field)
                else new ListBuffer[String]
              
              // sanitize the input - this might need to be expanded a bit
              if (value != null) values += nullCharTransform(value)
              map += (field -> values)
            }
          case GS =>
            // GS lines represent (among other things) proteins comprising a
            // protein family - store the UniProtKB accession numbers
            val GS_ANNOTATION(protein, field, value) = rest
            if (field == AC) {
              val UNIPROT_PROTEIN_AC(proteinAccession) = value
              memberProteins += proteinAccession
            }
          case _ =>
            // GR and GC lines don't have information that we care about
        }
      }
      else {
        // this line doesn't match anything we know about, so skip it
        // is this too optimistic? should we throw?
      }
    }
  }
}