package org.marktomko.pfamskos

import scala.collection.mutable.Set

class ProteinDatabaseHandler(val clanMembershipDB: MembershipDatabase, val familydb: Set[String], val familyMembershipDB: MembershipDatabase, val proteindb: Set[String]) extends StockholmRecordHandler {
  override def apply(record: StockholmRecord): Unit = {
    // if the record is a protein clan, store its member families in the database
    StockholmRecordHandler.getType(record) match {
      case "Clan" =>
        for (family <- record.memberFamilies) {
          // record which clan this family belongs to
          clanMembershipDB.addMember(family, StockholmRecordHandler.getAccession(record))
          // keep track of the families we've seen
          familydb += family
        }
      case "Family" =>
        for (protein <- record.memberProteins) {
          // record which family this protein belongs to
          familyMembershipDB.addMember(protein, StockholmRecordHandler.getAccession(record))
          
          // keep track of the proteins we've seen
          proteindb += protein
        }
      case _ =>
    }
  }
}