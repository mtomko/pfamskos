package org.marktomko.pfamskos

class ProteinClanDatabaseHandler(val clanMembershipDB: ClanMembershipDatabase) extends RecordHandler {
  override def apply(record: StockholmRecord): Unit = {
    // if the record is a protein clan, store its member families in the database
    if (RecordHandler.getType(record) == "Clan") {
      for(family <- record.memberFamilies) {
        clanMembershipDB.addProtein(family, RecordHandler.getAccession(record))
      }
    }
  }
}