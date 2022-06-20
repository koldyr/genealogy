package com.koldyr.genealogy.ui

/**
 * Description of the SearchData class
 *
 * @author Dzianis Halitski
 * @version 1.0
 * @created 11/14/2019
 */
data class SearchData(
    val input: String,
    val matchCase: Boolean,
    val wholeWord: Boolean
)