package org.marktomko.pfamskos.uniprot

trait UniprotRecordHandler {
  /**
   * Applies the record handler to the provided record. This trait must
   * have side effects, as it does not allow any return values.
   * @param record
   */
  def apply(accession: String, name: String): Unit
}