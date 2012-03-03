package org.marktomko.pfamskos

import org.junit.Test
import org.junit.Assert.assertEquals
import java.io.ByteArrayOutputStream

@Test
class SkosWriterTest {
  val conceptScheme = """<?xml version='1.0' encoding='UTF-8' standalone='yes'?>
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:dc="http://purl.org/dc/terms/" xmlns:uni="http://purl.uniprot.org/core/" xmlns:pfam="http://web.simmons.edu/~tomko/pfam#">
  <skos:ConceptScheme rdf:about="http://pfam.sanger.ac.uk">
  <skos:hasTopConcept rdf:resource="http://pfam.sanger.ac.uk/clan/"/>
  <skos:hasTopConcept rdf:resource="http://pfam.sanger.ac.uk/family/"/>
  <dc:title>Pfam</dc:title>
  </skos:ConceptScheme>
</rdf:RDF>"""

  val concept = """<?xml version='1.0' encoding='UTF-8' standalone='yes'?>
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:dc="http://purl.org/dc/terms/" xmlns:uni="http://purl.uniprot.org/core/" xmlns:pfam="http://web.simmons.edu/~tomko/pfam#">
  <rdf:Description rdf:about="http://pfam.sanger.ac.uk/family/PF01443">
  <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
  <skos:inScheme rdf:resource="http://pfam.sanger.ac.uk"/>
  <skos:prefLabel>Viral helicase1</skos:prefLabel>
  <skos:narrower rdf:resource="http://www.uniprot.org/uniprot/P04867"/>
  <skos:broader rdf:resource="http://pfam.sanger.ac.uk/clan/CL0023"/>
  </rdf:Description>
</rdf:RDF>"""

  @Test
  def testWriteConceptScheme() {
    val stream = new ByteArrayOutputStream()
    val writer = new SkosWriter(stream)

    writer.writeConceptScheme("http://pfam.sanger.ac.uk", List("http://pfam.sanger.ac.uk/clan/", "http://pfam.sanger.ac.uk/family/"), Map((writer.DC, "title") -> "Pfam"))
    writer.close()

    val result = stream.toString("UTF-8")

    assertEquals(conceptScheme, result)
  }

  @Test
  def testWriteConcept() {
    val stream = new ByteArrayOutputStream()
    val writer = new SkosWriter(stream)

    writer.writeConcept(
      "http://pfam.sanger.ac.uk/family/PF01443",
      "http://pfam.sanger.ac.uk",
      "Viral helicase1",
      List(),
      List("http://pfam.sanger.ac.uk/clan/CL0023"),
      List("http://www.uniprot.org/uniprot/P04867"),
      Map(),
      Map()

    )
    writer.close()

    val result = stream.toString("UTF-8")
    assertEquals(concept, result)
  }
}