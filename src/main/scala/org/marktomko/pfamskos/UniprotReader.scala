package org.marktomko.pfamskos

import scala.collection.mutable.Map
import scala.io.Source

import java.io.InputStream

/**
 * Reads a Uniprot file and records only the accession and preferred name.
 * 
 * @author Mark Tomko, (c) 2011
 */
object UniprotReader {
  val START = """%ID[^\n]+""".r
  val ACCESSION = """^AC[ ]+([A-Z0-9]+);"""r
  val NAME = """^DE[ ]+RecName: Full= ([A-Za-z0-9\/-]+);$""".r
  val END = """^//$""".r
  
  def read(stream: InputStream, handler: UniprotRecordHandler) {
    var accession = ""
    var name = ""
    for (line <- Source.fromInputStream(stream, "UTF-8").getLines) {
      if (line == START) {
        accession = ""
        name = ""
      } else if (line == END) {
        handler(accession, name)
      } else if (line startsWith "AC") {
        val ACCESSION(acc) = line
        accession = acc
      } else if (line startsWith "DE   RecName:") {
        val NAME(n) = line
        name = n
      }
    }
  }
}