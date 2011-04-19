package org.marktomko.pfamskos

object Pfam {
  val PFAM_URL = "http://pfam.sanger.ac.uk"
  val UNIPROT_URL = "http://www.uniprot.org/uniprot"
}

object PfamSkos {
  val SCHEME_URL = "http://web.simmons.edu/~tomko/pfam"
  val NULL_CLAN = SCHEME_URL+"/clanless"
  val TOP_CONCEPTS = List(Pfam.PFAM_URL+"/clan/browse", NULL_CLAN)
}