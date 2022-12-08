package com.koldyr.genealogy.model.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import com.koldyr.genealogy.model.Gender

/**
 * Description of class GenderConverter
 * @created: 2021-09-27
 */
@Converter
class GenderConverter: AttributeConverter<Gender, Char> {

    override fun convertToDatabaseColumn(gender: Gender): Char {
        return gender.name.elementAt(0)
    }

    override fun convertToEntityAttribute(gender: Char): Gender {
        return when (gender) {
            'M' -> Gender.MALE
            'F' -> Gender.FEMALE
            else -> throw IllegalArgumentException("Wrong value '$gender' for gender")
        }
    }
}
