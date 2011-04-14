package org.marktomko.pfamskos

class ProteinClanDatabaseHandler(val clanMembershipDB: ClanMembershipDatabase) extends RecordHandler {
  override def apply(record: StockholmRecord): Unit = {
    // if the record is a protein clan, store its member proteins in the database
    if (record.memberProteins.size > 0) {
      for(protein <- record.memberProteins) {
        clanMembershipDB.addProtein(protein, getAccession(record))
      }
    }
  }
}