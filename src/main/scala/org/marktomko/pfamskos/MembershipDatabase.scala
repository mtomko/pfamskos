package org.marktomko.pfamskos

/**
 * Describes an interface for a class that tracks the membership of
 * elements in specific groups.
 *
 * @author Mark Tomko, (c) 2011
 */
trait MembershipDatabase {
  /**
   * Stores the group associated with a given member
   */
  def addMember(member: String, group: String): Unit
  /**
   * Retrieves the group associated with a given member; returns null
   * if no group is found.
   */
  def groupFor(member: String): String
}

trait MultiMembershipDatabase {
  /**
   * Stores the group associated with a given member
   */
  def addMember(member: String, group: String): Unit
  /**
   * Retrieves the group associated with a given member; returns null
   * if no group is found.
   */
  def groupsFor(member: String): Set[String]
}