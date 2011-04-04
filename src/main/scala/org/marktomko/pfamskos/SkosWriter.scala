package org.marktomko.pfamskos

import java.io.OutputStream

import javax.xml.stream.XMLOutputFactory

import org.codehaus.staxmate.SMOutputFactory

class SkosWriter {
  def write(records: List[StockholmRecord], stream: OutputStream) {
    val factory = XMLOutputFactory.newInstance()
    val sw = factory.createXMLStreamWriter(stream, "UTF-8")

    val doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true)

    // defines linefeed to use, spaces for indentation (from 1, step by 2)
    doc.setIndentation("\n  ", 1, 2)
    val rdf = doc.getNamespace("http://www.w3.org/1999/02/22-df-syntax-ns#", "rdf")
    val rdfs = doc.getNamespace("http://www.w3.org/2000/01/rdf-schema#", "rdfs")
    val skos = doc.getNamespace("http://www.w3.org/2004/02/skos/core#", "skos")
    val dc = doc.getNamespace("http://purl.org/dc/terms/", "dc")

    val root = doc.addElement(rdf, "RDF")
    root.predeclareNamespace(rdf)
    root.predeclareNamespace(rdfs)
    root.predeclareNamespace(skos)
    root.predeclareNamespace(dc)

    val cs = root.addElement(skos, "ConceptScheme")
    cs.addAttribute(rdf, "about", "http://pfam.sanger.uk.ac")
    cs.addElement(dc, "title").addCharacters("Pfam")
    cs.addElement(dc, "date").addCharacters("2009-07-09")
    cs.addElement(dc, "creator").addCharacters("Sanger Institute")

    doc.closeRoot
  }

}

object SkosWriter {
  def main(args: Array[String]): Unit = {
    (new SkosWriter).write(List(), System.out)
  }
}