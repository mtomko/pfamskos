package org.marktomko.pfamskos

/**
 * Describes an interface for a class that tracks the membership of
 * proteins in specific clans.
 * 
 * @author Mark Tomko, (c) 2011
 */
trait ClanMembershipDatabase {
    /**
     * Stores the clan associated with a given protein
     */
    def addProtein(protein: String, clan: String): Unit
    /**
     * Retrieves the clan associated with a given protein; returns null
     * if no clon is found.
     */
    def clanFor(protein: String): String
}