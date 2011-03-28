package org.marktomko.pfamskos

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary._


object JenaApp {
  def main(args : Array[String]) : Unit = {
      val model = ModelFactory.createDefaultModel
      
      val resource = model.createResource("http://pfam.sanger.ac.uk")
      
      model.write(System.out)
  }
}
