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

  val RDF = doc.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf")
  val RDFS = doc.getNamespace("http://www.w3.org/2000/01/rdf-schema#", "rdfs")
  val SKOS = doc.getNamespace("http://www.w3.org/2004/02/skos/core#", "skos")
  val DC = doc.getNamespace("http://purl.org/dc/terms/", "dc")
  val UNIPROT = doc.getNamespace("http://purl.uniprot.org/core/", "uni")

  val root = doc.addElement(RDF, "RDF")
  root.predeclareNamespace(RDF)
  root.predeclareNamespace(RDFS)
  root.predeclareNamespace(SKOS)
  root.predeclareNamespace(DC)
  root.predeclareNamespace(UNIPROT)

  /**
   * Writes a description of the concept scheme
   * @param about
   * @param topConcepts
   * @param metadata
   */
  def writeConceptScheme(about: String, topConcepts: List[String], metadata: Map[(SMNamespace, String), String]) {
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
   * @param nonCharMetadata
   */
  def writeConcept(about: String, scheme: String, prefLabel: String, altLabels: Iterable[String], broaderTerms: Iterable[String], narrowerTerms: Iterable[String], metadata: Map[(SMNamespace, String), String], nonCharMetadata: Map[(SMNamespace, String), List[(SMNamespace, String, String)]]) {
    val conceptElt = writeRdfDescription(root, about)
    writeSimpleElement(conceptElt, RDF, "type", Map((RDF, "resource") -> (SKOS.getURI + "Concept")))
    writeSimpleElement(conceptElt, SKOS, "inScheme", Map((RDF, "resource") -> scheme))

    val prefLabelElt = writeSimpleElement(conceptElt, SKOS, "prefLabel", Map())
    prefLabelElt.addCharacters(prefLabel)
    
    for(label <- altLabels) {
      val labelElt = writeSimpleElement(conceptElt, SKOS, "altLabel", Map())
      labelElt.addCharacters(label)
    }
    
    for(term <- narrowerTerms)
      writeSimpleElement(conceptElt, SKOS, "narrower", Map((RDF, "resource") -> term))
    
    for(term <- broaderTerms)
      writeSimpleElement(conceptElt, SKOS, "broader", Map((RDF, "resource") -> term))

    for (datum <- metadata)
      conceptElt.addElement(datum._1._1, datum._1._2).addCharacters(datum._2)

    for (datum <- nonCharMetadata) {
      val elt = conceptElt.addElement(datum._1._1, datum._1._2)
      for (attr <- datum._2) {
        elt.addAttribute(attr._1, attr._2, attr._3)
      }
    }
  }

  def writeRdfDescription(parent: SMOutputElement, about: String): SMOutputElement =
    writeSimpleElement(parent, RDF, "Description", Map((RDF, "about") -> about))

  def writeSequenceAlignment(protein: String, family: String, alignment: String, range:(Int, Int)) {
    val parent = writeRdfDescription(root, protein)
    writeSimpleElement(parent, RDF, "type", Map((RDF, "resource") -> (UNIPROT.getURI + "Sequence")))
    writeSimpleElement(parent, UNIPROT, "sequenceFor", Map((RDF, "about") -> family))

    val begin = writeSimpleElement(parent, UNIPROT, "begin", Map())
    begin.addCharacters(range._1.toString)

    val end = writeSimpleElement(parent, UNIPROT, "end", Map())
    end.addCharacters(range._2.toString)

    val sequence = writeSimpleElement(parent, UNIPROT, "sequence", Map())
    sequence.addCharacters(alignment)
  }

  /**
   * Writes the close of the document to the stream
   */
  def close() {
    doc.closeRoot()
  }

  private def writeSimpleElement(parent: SMOutputElement, ns: SMNamespace, name: String, attributes: Map[(SMNamespace, String), String]): SMOutputElement = {
    val elt = parent.addElement(ns, name)
    attributes foreach ((attr) => (elt.addAttribute(attr._1._1, attr._1._2, attr._2)))
    elt
  }
}