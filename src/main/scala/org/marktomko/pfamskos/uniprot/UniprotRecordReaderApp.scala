package org.marktomko.pfamskos.uniprot

class PrintPairHandler extends UniprotRecordHandler {
  override def apply(accession: String, name: String): Unit = {
    println("Accession: " + accession + " -> " + name)
  }
}

object UniprotRecordReaderApp {
  def main(args : Array[String]) : Unit = {
     val file = args(0)
     
     val stream = new java.util.zip.GZIPInputStream(new java.io.FileInputStream(file))
     
     UniprotReader.read(stream, new PrintPairHandler)
  }
}
