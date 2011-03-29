package org.marktomko.pfamskos

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import scala.io.Source

import scala.util.matching.Regex

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

  // this splits a #=GS line into a protein, subfield, and subfield value
  val GS_ANNOTATION = """([A-Z\d-_/]+)[ ]+([A-Z]{2})[ ]*([^\n]+)""".r

  /**
   * Reads in a Stockholm file, building a representation in main memory.
   * @param file The name of the Stockholm file to read
   * @return A list of [[StockholmRecord]]
   */
  def read(file: String): List[StockholmRecord] = {
    val records = new ListBuffer[StockholmRecord]

    var id: String = null
    var map: Map[String, ListBuffer[String]] = null
    var memberFamilies: ListBuffer[String] = null
    var memberProteins: ListBuffer[String] = null
    
    val lines = Source.fromFile(file, "UTF-8").getLines
    for (line <- lines) {
      if (line == START) {
        map = new HashMap[String, ListBuffer[String]]
        memberFamilies = new ListBuffer[String]
        memberProteins = new ListBuffer[String]
      } else if (line == END) {
        records += new StockholmRecord(id, map, memberFamilies.toList, memberProteins.toList)
      } else if (line startsWith FIELD_PREFIX) {
        val NON_ALIGNMENT_LINE(prefix, rest) = line
        prefix match {
          case GF =>
            val GF_ANNOTATION(field, value) = rest
            if (field == ID) {
              id = value
            } else {
              field match {
                case AC | CC | CL | DE | MB | RM | TP =>
                  val values =
                      if (map.contains(field)) map(field)
                      else new ListBuffer[String]
                  values += value
                  map += (field -> values)
                case _ =>
              }
            }
          case GS =>
            val GS_ANNOTATION(protein, field, value) = rest
            if (field == AC) {
              memberProteins += value
            }
          case GR =>
          case GC =>
        }
      }
      else {
          // skip it
      }
    }

    records.toList
  }
}

object StockholmRecordReaderApp {
  def main(args: Array[String]): Unit = {
    val file = args(0)

    val records = StockholmRecordReader.read(file)

    for (record <- records) {
      println(record.id)
    }
  }
}