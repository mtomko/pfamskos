package org.marktomko.util

trait StringTransform {
  def apply(in: String): String
}

class CompositeStringTransform(val transform: List[StringTransform]) extends StringTransform {
  override def apply(in: String): String = {
    applySuccessive(transform, in)
  }

  def applySuccessive(transform: List[StringTransform], s: String): String = {
    transform match {
      case t :: xt => applySuccessive(xt, t(s))
      case Nil => s
    }
  }
}

class SubstitutionStringTransform(val search: String, val replace: String) extends StringTransform {
  override def apply(in: String): String = {
    in.replaceAll(search, replace)
  }
}