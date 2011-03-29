package org.marktomko.pfamskos

import java.io.OutputStream

import com.ctc.wstx.stax.WstxOutputFactory

class SkosWriter {
    def write(records: List[StockholmRecord], stream: OutputStream) {
      val factory = new WstxOutputFactory
      factory.configureForXmlConformance()
      
      val writer = factory.createXMLStreamWriter(stream, "UTF-8")
      writer.setPrefix("rdf", "http://www.w3.org/1999/02/22-df-syntax-ns#")
      writer.setPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
      writer.setPrefix("skos", "http://www.w3.org/2004/02/skos/core#")
      writer.setPrefix("dc", "http://purl.org/dc/terms/")
      
      writer.writeStartDocument("UTF-8", "1.0")
      
      // open RDF
      writer.writeStartElement("http://www.w3.org/1999/02/22-df-syntax-ns#", "RDF")
      writer.writeNamespace("rdf", "http://www.w3.org/1999/02/22-df-syntax-ns#")
      writer.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
      writer.writeNamespace("skos", "http://www.w3.org/2004/02/skos/core#")
      writer.writeNamespace("dc", "http://purl.org/dc/terms/")
      
      writer.writeStartElement("http://www.w3.org/2004/02/skos/core#", "ConceptScheme")
      writer.writeAttribute("http://www.w3.org/1999/02/22-df-syntax-ns#", "about", "http://pfam.sanger.uk.ac")
      
      writer.writeStartElement("http://purl.org/dc/terms/", "title")
      writer.writeCharacters("Pfam")
      writer.writeEndElement
      
      writer.writeStartElement("http://purl.org/dc/terms/", "date")
      writer.writeCharacters("2009-07-09")
      writer.writeEndElement
      
      writer.writeStartElement("http://purl.org/dc/terms/", "creator")
      writer.writeCharacters("Sanger Institute")
      writer.writeEndElement
      
      writer.writeEndElement
      
      // close RDF
      writer.writeEndElement
      writer.writeEndDocument
    }
}

object SkosWriter {
  def main(args: Array[String]): Unit = {
    (new SkosWriter).write(List(), System.out)
  }
}