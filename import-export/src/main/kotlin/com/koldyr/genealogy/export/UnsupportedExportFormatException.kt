package com.koldyr.genealogy.export

/**
 * Description of the UnsupportedExportFormatException class
 *
 * @author d.halitski@gmail.com
 * @created 2022-07-18
 */
class UnsupportedExportFormatException(error: String) : IllegalArgumentException(error)