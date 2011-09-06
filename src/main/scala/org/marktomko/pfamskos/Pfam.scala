package org.marktomko.pfamskos

object Pfam {
  val PFAM_URL = "http://pfam.sanger.ac.uk"
  val UNIPROT_URL = "http://purl.uniprot.org/uniprot"
  
  def getClanUrl(clan: String): String = {
    PFAM_URL + "/clan/" + clan
  }
      
  def getFamilyUrl(family: String): String = {
    PFAM_URL + "/family/" + family
  }
  
  def getProteinUrl(protein: String): String = {
    UNIPROT_URL + "/" + protein
  }
}
