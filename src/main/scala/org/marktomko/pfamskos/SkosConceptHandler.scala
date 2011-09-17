package org.marktomko.pfamskos

import org.marktomko.pfamskos.stockholm.StockholmRecord
import org.marktomko.pfamskos.stockholm.StockholmRecordHandler

import org.codehaus.staxmate.out.SMNamespace
import org.marktomko.util.SubstitutionStringTransform
import scala.collection.mutable.{HashMap, Set}

/**
 * This class handles each Stockholm Record and produces a SKOS concept. It
 * necessarily must track clan membership, a list of clans, and a list of
 * protein families that do not belong to any clan.
 * 
 * @author Mark Tomko, (c) 2011
 */
class SkosConceptHandler(val clanMembershipDB: MembershipDatabase, val familydb: Set[String], val skosWriter: SkosWriter) extends StockholmRecordHandler {
  val INTERPRO_REGEX = """^INTERPRO; ([A-Z0-9]+);$""".r
  val PROSITE_REGEX = """^PROSITE; ([A-Z0-9]+);$""".r
  val interproUrlPrefix = "http://www.ebi.ac.uk/interpro/IEntry?ac="
  val prositeUrlPrefix = "http://prosite.expasy.org/cgi-bin/prosite/prosite-search-ac?"

  val pubmedUrlPrefix = "http://www.ncbi.nlm.nih.gov/pubmed/"
  val citationAttr = (skosWriter.UNIPROT, "citation")

  val labelTransform = new SubstitutionStringTransform("_", " ")

  override def apply(record: StockholmRecord) {
    val recordType = StockholmRecordHandler.getType(record)
    val conceptClass = recordType match {
      case "Clan" => "Clan"
      case "Domain" => "Family"
      case "Family" => "Family"
      case "Motif" => "Family"
      case "Repeat" => "Family"
    }

    // build a map of general metadata
    val metadata = new HashMap[(SMNamespace, String), String]
    val nonCharMetadata = new HashMap[(SMNamespace, String), List[(SMNamespace, String, String)]]

    // get the record's accession - we'll use this in URLs
    val accession = StockholmRecordHandler.getAccession(record)

    // put the raw accession information into the map as externalId
    metadata += (skosWriter.SKOS, "externalId") -> StockholmRecordHandler.getRawAccession(record)

    // translate the definition to the scope note
    val de = getMultiLineField(record, "DE")
    if (de != null) {
      metadata += (skosWriter.SKOS, "scopeNote") -> de
    }

    // translate the comments to the definition
    val cc = getMultiLineField(record, "CC")
    if (cc != null)
      metadata += (skosWriter.SKOS, "definition") -> cc

    val about =
      if (conceptClass == "Family")
        Pfam.getFamilyUrl(accession)
      else
        Pfam.getClanUrl(accession)

    val preferred = labelTransform(record.id)
    val alternate =
      if (record.id == preferred)
        List()
      else
        List(record.id)

    val broader =
      if (conceptClass == "Family") {
        val clan = clanMembershipDB.groupFor(accession)
        if (clan == null)
          List()
        else
          List(Pfam.getClanUrl(clan))
      } else {
        // clans have a single parent
        List()
      }

    val narrower =
      if (conceptClass == "Family") {
        record.memberProteins.map((protein) => {
          Pfam.UNIPROT_URL + "/" + protein
        })
      } else {
        record.memberFamilies.map(Pfam.getFamilyUrl(_))
      }

    if (record.getFields().contains("RM")) {
      val pmids = record.getValues("RM")
      for (pmid <- pmids) {
        val pmidTuple = (skosWriter.RDF, "resource", pubmedUrlPrefix + pmid)
        if (nonCharMetadata.contains(citationAttr)) {
          val citations:List[(SMNamespace, String, String)] = nonCharMetadata(citationAttr)
          nonCharMetadata.put(citationAttr, citations :+ pmidTuple)
        }
        else {
          nonCharMetadata.put(citationAttr, List(pmidTuple))
        }
      }
    }

    val relatedReferences =
      if (record.getFields().contains("DR")) {
        for (dr <- record.getValues("DR")) yield {
          dr match {
            case INTERPRO_REGEX(interproAccession) => {
              Some(interproUrlPrefix + interproAccession)
            }
            case PROSITE_REGEX(prositeAccession) => {
              Some(prositeUrlPrefix + prositeAccession)
            }
            case _ => None
          }
        }
      }
      else
        List()

    val related: Iterable[String] = relatedReferences filter { _ != None } map { _.get }

    // housekeeping - if we're about to write a family, remove it from the family db
    if (conceptClass == "Family")
      familydb -= accession

    skosWriter.writeConcept(about, (skosWriter.PFAM, recordType), Pfam.PFAM_URL, preferred, alternate, broader, narrower, related, metadata.toMap, nonCharMetadata.toMap)
    for (((protein, (start, end)), alignment) <- record.proteinSequenceMap) {
      val proteinUrl = Pfam.UNIPROT_URL + "/" + protein
      skosWriter.writeSequenceAlignment(proteinUrl, Pfam.getFamilyUrl(accession), alignment, (start, end))
    }
  }

  def getMultiLineField(record: StockholmRecord, field: String): String =
    if (record.getFields().contains(field))
    // joins successive values with a single space
      record.getValues(field).reduceLeft(_ + " " + _)
    else
      null

}