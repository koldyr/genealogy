package com.koldyr.genealogy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


/**
 * Description of class Genealogy
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-25
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.koldyr.genealogy.persistence"])
@EntityScan("com.koldyr.genealogy.model")
open class Genealogy

fun main(args: Array<String>) {
    runApplication<Genealogy>(*args)
}
