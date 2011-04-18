package org.marktomko.pfamskos

import java.io.OutputStream

import javax.xml.stream.XMLOutputFactory

import org.codehaus.staxmate.out.SMNamespace
import org.codehaus.staxmate.out.SMOutputElement
import org.codehaus.staxmate.SMOutputFactory

/**
 * Writes an RDF/XML representation of a SKOS scheme to a stream.
 * 
 * @author Mark Tomko, (c) 2011
 */
class SkosWriter(stream: OutputStream) {
  val factory = XMLOutputFactory.newInstance()
  val sw = factory.createXMLStreamWriter(stream, "UTF-8")
  val doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true)
  // defines linefeed to use, spaces for indentation (from 1, step by 2)
  doc.setIndentation("\n  ", 1, 2)

  val RDF = doc.getNamespace("http://www.w3.org/1999/02/22-df-syntax-ns#", "rdf")
  val RDFS = doc.getNamespace("http://www.w3.org/2000/01/rdf-schema#", "rdfs")
  val SKOS = doc.getNamespace("http://www.w3.org/2004/02/skos/core#", "skos")
  val DC = doc.getNamespace("http://purl.org/dc/terms/", "dc")

  val root = doc.addElement(RDF, "RDF")
  root.predeclareNamespace(RDF)
  root.predeclareNamespace(RDFS)
  root.predeclareNamespace(SKOS)
  root.predeclareNamespace(DC)

  /**
   * Writes a description of the concept scheme
   * @param about
   * @param topConcepts
   * @param metadata
   */
  def writeConceptScheme(about: String, topConcepts: List[String], metadata: Map[Tuple2[SMNamespace, String], String]) {
    val cs = root.addElement(SKOS, "ConceptScheme")
    cs.addAttribute(RDF, "about", about)
    
    for(topConcept <- topConcepts) {
      writeSimpleElement(cs, SKOS, "hasTopConcept", Map((RDF, "resource") -> topConcept))
    }
    metadata foreach ((md) => cs.addElement(md._1._1, md._1._2).addCharacters(md._2))
  }
  
  /**
   * Writes a description of a concept
   * @param about
   * @param scheme
   * @param prefLabel
   * @param altLabels
   * @param broaderTerms
   * @param narrowerTerms
   * @param metadata
   */
  def writeConcept(about: String, scheme: String, prefLabel: String, altLabels: Iterable[String], broaderTerms: Iterable[String], narrowerTerms: Iterable[String], metadata: Map[Tuple2[SMNamespace, String], String]) {
    val conceptElt = writeSimpleElement(root, SKOS, "Concept", Map((RDF, "about") -> about))    
    writeSimpleElement(conceptElt, SKOS, "inScheme", Map((RDF, "resource") -> scheme))

    val prefLabelElt = writeSimpleElement(conceptElt, SKOS, "prefLabel", Map())
    prefLabelElt.addCharacters(prefLabel)
    
    for(label <- altLabels) {
      val labelElt = writeSimpleElement(conceptElt, SKOS, "altLabel", Map())
      labelElt.addCharacters(label)
    }
    
    for(term <- narrowerTerms) {
      writeSimpleElement(conceptElt, SKOS, "narrower", Map((RDF, "resource") -> term))
    }
    
    for(term <- broaderTerms) {
      writeSimpleElement(conceptElt, SKOS, "broader", Map((RDF, "resource") -> term))
    }
  }

  /**
   * Writes the close of the document to the stream
   */
  def close() {
    doc.closeRoot
  }

  private def writeSimpleElement(parent: SMOutputElement, ns: SMNamespace, name: String, attributes: Map[Tuple2[SMNamespace, String], String]): SMOutputElement = {
    val elt = parent.addElement(ns, name)
    attributes foreach ((attr) => (elt.addAttribute(attr._1._1, attr._1._2, attr._2)))
    elt
  }
}

/**
 * This object serves primarily as a test driver for the SkosWriter class. It
 * should be deleted and replaced with proper unit tests.
 * 
 * @author Mark Tomko, (c) 2011
 */
object SkosWriter {
  def main(args: Array[String]): Unit = {
    val skosWriter = new SkosWriter(System.out)
    skosWriter.writeConceptScheme("http//pfam.sanger.uk.ac",
                                  List("http://pfam.sanger.ac.uk/clan/browse", "http://pfam.sanger.ac.uk/family/browse"),
                                  Map((skosWriter.DC, "title") -> "Pfam",
                                      (skosWriter.DC, "date") -> "2009-07-09",
                                      (skosWriter.DC, "creator") -> "Sanger Institute"))
    skosWriter.writeConcept("http://pfam.sanger.ac.uk/clan/browse", "http://pfam.sanger.ac.uk", "Pfam Clan", List(), List(), List("http://pfam.sanger.ac.uk/clan/CL0023"), Map())
    skosWriter.close
  }
}