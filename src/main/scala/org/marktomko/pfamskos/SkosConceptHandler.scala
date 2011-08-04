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
  val pubmedUrlPrefix = "http://www.ncbi.nlm.nih.gov/pubmed/"
  val citationAttr = (skosWriter.UNIPROT, "citation")
  val labelTransform = new SubstitutionStringTransform("_", " ")

  override def apply(record: StockholmRecord) {
    val recordType = StockholmRecordHandler.getType(record)
    if (recordType == "Domain" || recordType == "Repeat" || recordType == "Motif") {
      // skip this record
    } else {
      // build a map of general metadata
      val metadata = new HashMap[(SMNamespace, String), String]
      val nonCharMetadata = new HashMap[(SMNamespace, String), List[(SMNamespace, String, String)]]
      
      // get the record's accession - we'll use this in URLs
      val ac = StockholmRecordHandler.getRawAccession(record)
      val accession = StockholmRecordHandler.getAccession(record)

      // put the raw accession information into the map as externalId
      metadata += (skosWriter.SKOS, "externalId") -> ac
      
      // translate the definition to the scope note
      val de = getMultiLineField(record, "DE")
      if (de != null) {
        metadata += (skosWriter.SKOS, "scopeNote") -> de
      }
      
      // translate the comments to the definition
      val cc = getMultiLineField(record, "CC") 
      if (cc != null) {
        metadata += (skosWriter.SKOS, "definition") -> cc
      }

      val about = 
        if (recordType == "Family") {
          Pfam.getFamilyUrl(accession)
        } else {
          Pfam.getClanUrl(accession)
        }
 
      val preferred = labelTransform(record.id)
      val alternate =
        if (record.id == preferred) {
          List()
        } else {
          List(record.id)
        }

      val broader =
        if (recordType == "Family") {
          val clan = clanMembershipDB.groupFor(accession)
          if (clan == null) {
            List()
          } else {
            List(Pfam.getClanUrl(clan))
          }
        } else {
          // clans have a single parent
          List()
        }

      val narrower =
        if (recordType == "Family") {
          record.memberProteins.map((protein) => {
            Pfam.UNIPROT_URL + "/" + protein
          })
          //List() // temporarily ignore all proteins!
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

      // housekeeping - if we're about to write a family, remove it from the family db
      if (recordType == "Family") {
        familydb -= accession
      }

      skosWriter.writeConcept(about, Pfam.PFAM_URL, preferred, alternate, broader, narrower, metadata.toMap, nonCharMetadata.toMap)
    }
    
    def getMultiLineField(record: StockholmRecord, field: String): String = {
      if (record.getFields().contains(field)) {
        // joins successive values with a single space
        record.getValues(field).reduceLeft(_ + " " + _)
      } else {
        null
      }
    }
  }
}