package org.marktomko.pfamskos

import org.semanticweb.skosapibinding.SKOSManager
import org.semanticweb.skosapibinding.SKOSFormatExt
import org.semanticweb.skos.AddAssertion
import org.semanticweb.skos.SKOSChange
   
import java.net.URI
import java.util.ArrayList

object SkosapiApp {
  def main(args : Array[String]) : Unit = {
      val manager = new SKOSManager

      val baseURI = "http://www.semanticweb.org/skos/example2.rdf"
      val dataset = manager.createSKOSDataset(URI.create(baseURI))
      
      val df = manager.getSKOSDataFactory
      val conceptScheme1 = df.getSKOSConceptScheme(URI.create(baseURI + "#conceptScheme1"))
      val conceptA = df.getSKOSConcept(URI.create(baseURI + "#conceptA"))
      val conceptB = df.getSKOSConcept(URI.create(baseURI + "#conceptB"))
      val conceptC = df.getSKOSConcept(URI.create(baseURI + "#conceptC"))
      val conceptD = df.getSKOSConcept(URI.create(baseURI + "#conceptD"))
      
      val entityAssertion1 = df.getSKOSEntityAssertion(conceptScheme1)
      val entityAssertion2 = df.getSKOSEntityAssertion(conceptA)
      val entityAssertion3 = df.getSKOSEntityAssertion(conceptB)
      val entityAssertion4 = df.getSKOSEntityAssertion(conceptC)
      val entityAssertion5 = df.getSKOSEntityAssertion(conceptD)
      
      val inScheme = df.getSKOSInSchemeProperty()
      
      val propertyAssertion1 = df.getSKOSObjectRelationAssertion(conceptA, inScheme, conceptScheme1)
      val propertyAssertion2 = df.getSKOSObjectRelationAssertion(conceptB, inScheme, conceptScheme1)
      val propertyAssertion3 = df.getSKOSObjectRelationAssertion(conceptC, inScheme, conceptScheme1)
      val propertyAssertion4 = df.getSKOSObjectRelationAssertion(conceptD, inScheme, conceptScheme1)
      
      val addAssertions = new ArrayList[SKOSChange]()
      addAssertions.add(new AddAssertion(dataset, entityAssertion1))
      addAssertions.add(new AddAssertion(dataset, entityAssertion2))
      addAssertions.add(new AddAssertion(dataset, entityAssertion3))
      addAssertions.add(new AddAssertion(dataset, entityAssertion4))
      addAssertions.add(new AddAssertion(dataset, entityAssertion5))
  
      addAssertions.add(new AddAssertion(dataset, propertyAssertion1))
      addAssertions.add(new AddAssertion(dataset, propertyAssertion2))
      addAssertions.add(new AddAssertion(dataset, propertyAssertion3))
      addAssertions.add(new AddAssertion(dataset, propertyAssertion4))
      
      manager.applyChanges(addAssertions)
      
      manager.save(dataset, SKOSFormatExt.RDFXML, URI.create("file:/Users/mark/example2.rdf"));
  }
}
