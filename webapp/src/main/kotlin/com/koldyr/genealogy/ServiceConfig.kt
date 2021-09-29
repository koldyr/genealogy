package com.koldyr.genealogy

import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import com.koldyr.genealogy.services.GenealogyService
import com.koldyr.genealogy.services.GenealogyServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Description of class ServiceConfig
 * @created: 2021-09-28
 */
@Configuration
open class ServiceConfig {

    @Bean
    open fun genealogyService(personRepository: PersonRepository, familyRepository: FamilyRepository): GenealogyService {
        return GenealogyServiceImpl(personRepository, familyRepository)
    }
}
