package org.marktomko.pfamskos

import scala.collection.mutable.Set

/**
 * This class handles each Stockholm Record and produces a SKOS concept. It
 * necessarily must track clan membership, a list of clans, and a list of
 * protein families that do not belong to any clan.
 * 
 * @author Mark Tomko, (c) 2011
 */
class SkosConceptHandler(val clanMembershipDB: ClanMembershipDatabase, val clans: Set[String], val clanless: Set[String], val skosWriter: SkosWriter) extends RecordHandler {
  val SCHEME = "http://web.simmons.edu/~tomko/pfam"
  val NULL_CLAN = SCHEME + "/clanless"
  val CLAN = Pfam.PFAM_URL + "/clans/browse"
  val FAMILY = Pfam.PFAM_URL + "/family/browse"
  val PROTEIN = """([A-Z0-9]+)\.[^\n]+""".r

  val labelTransform = new SubstitutionStringTransform("_", " ")

  override def apply(record: StockholmRecord) {
    val recordType = RecordHandler.getType(record)
    if (recordType == "Domain") {
      // skip this record
    } else {
      val ac = RecordHandler.getRawAccession(record)
      val accession = RecordHandler.getAccession(record)

      // joins successive values with a single space
      val de = record.getValues("DE").reduceLeft(_ + " " + _)
      val metadata = Map((skosWriter.SKOS, "externalId") -> ac,
                         (skosWriter.SKOS, "definition") -> de)

      val typeURL =
        if (recordType == "Family") {
          "/family/"
        } else {
          "/clan/"
        }
      val about = Pfam.PFAM_URL + typeURL + accession

      val preferred = labelTransform(record.id)
      val alternate =
        if (record.id == preferred) {
          List()
        } else {
          List(record.id)
        }

      val broader =
        if (recordType == "Family") {
          val clan = clanMembershipDB.clanFor(accession)
          if (clan == null) {
            clanless += about
            NULL_CLAN
          } else {
            Pfam.PFAM_URL + "/clan/" + clan
          }
        } else {
          // clans have a single parent
          clans += about
          CLAN
        }

      val narrower =
        if (recordType == "Family") {
          record.memberProteins.map((prot) => {
            val PROTEIN(uniprot) = prot
            Pfam.UNIPROT_URL + "/" + uniprot
          })
        } else {
          record.memberFamilies.map(Pfam.PFAM_URL + "/family/" + _)
        }

      skosWriter.writeConcept(about, SCHEME, preferred, alternate, List(broader), narrower, metadata)
    }
  }
}