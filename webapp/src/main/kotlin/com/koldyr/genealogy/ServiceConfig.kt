package com.koldyr.genealogy

import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import com.koldyr.genealogy.services.FamilyService
import com.koldyr.genealogy.services.FamilyServiceImpl
import com.koldyr.genealogy.services.PersonService
import com.koldyr.genealogy.services.PersonServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Description of class ServiceConfig
 * @created: 2021-09-28
 */
@Configuration
open class ServiceConfig {

    @Bean
    open fun personService(personRepository: PersonRepository): PersonService {
        return PersonServiceImpl(personRepository)
    }

    @Bean
    open fun familyService(familyRepository: FamilyRepository): FamilyService {
        return FamilyServiceImpl(familyRepository)
    }
}
