package org.marktomko.pfamskos.uniprot

import scala.collection.mutable.ListBuffer
import scala.io.Source

import java.io.InputStream

/**
 * Reads a Uniprot file and records only the accession and preferred name.
 * 
 * @author Mark Tomko, (c) 2011
 */
object UniprotReader {
  val START = """ID""".r
  val ACCESSION_LINE = """^AC[ ]+([^\n]+)"""r
  val NAME = """^DE[ ]+RecName: Full=(.+);$""".r
  val END = """//""".r
  
  def read(stream: InputStream, handler: UniprotRecordHandler) {
    var accessions = ListBuffer[String]()
    var name = ""
    for (line <- Source.fromInputStream(stream, "UTF-8").getLines) {
      if (line startsWith "ID") {
        accessions = ListBuffer[String]()
        name = ""
      } else if (line startsWith "//") {
        for(accession <- accessions) {
          handler(accession, name)
        }
      } else if (line startsWith "AC") {
        val ACCESSION_LINE(accessionList) = line
        for (accession <- accessionList.split(";")) {
          accessions += accession.replace(" ", "")
        }
      } else if (line startsWith "DE   RecName:") {
        val NAME(n) = line
        name = n
      }
    }
  }
}