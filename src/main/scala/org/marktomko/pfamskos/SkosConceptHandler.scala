package org.marktomko.pfamskos

class SkosConceptHandler(val clanMembershipDB: ClanMembershipDatabase, val skosWriter: SkosWriter) extends RecordHandler {
  val CLAN = Pfam.PFAM_URL + "/clans/browse/"
  val FAMILY = Pfam.PFAM_URL + "/family/browse/"
  val PROTEIN = """([A-Z0-9])+;""".r

  override def apply(record: StockholmRecord) {
    val recordType = getType(record)
    if (recordType == "Domain") {
      // skip this record
    } else {
      // joins successive values with a single space
      val de = record.getValues("DE").reduceLeft(_ + " " + _)
      val metadata = Map((skosWriter.SKOS, "externalId") -> getRawAccession(record),
                         (skosWriter.SKOS, "definition") -> de)
      val broader =
        if (recordType == "Family") {
          val clan = clanMembershipDB.clanFor(getAccession(record))
          if (clan == null) {
            null
          }
          Pfam.PFAM_URL + "/clan/" + clan
        } else {
          // clans have a single parent
          CLAN
        }
      
      val narrower =
        if (recordType == "Family") {
          record.memberProteins.map(Pfam.UNIPROT_URL + "/" + _)
        }
        else {
          record.memberFamilies.map(Pfam.PFAM_URL + "/family/" + _)
        }
    }
  }
}