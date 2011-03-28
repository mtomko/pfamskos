package org.marktomko.pfamskos

import org.scardf.Vocabulary
import org.scardf.UriRef
import org.scardf.Graph
import org.scardf.RDF
import org.scardf.jena._

/**
 * Hello world!
 *
 */
object App extends Application {
  val skos = Vocabulary("http://www.w3.org/2004/02/skos/core#")
  val dc = Vocabulary( "http://purl.org/dc/elements/1.1/#" )

  val pfam = UriRef("http://pfam.sanger.ac.uk")
 
  val g = Graph.build( pfam -( 
          RDF.Type -> skos\"ConceptScheme" 
  ) )
  
  val l = List(1, 2)
  
  println(g.rend)
}
