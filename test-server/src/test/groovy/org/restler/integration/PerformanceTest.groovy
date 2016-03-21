package org.restler.integration

import org.restler.Restler
import org.restler.Service
import org.restler.integration.springdata.Person
import org.restler.integration.springdata.PersonsRepository
import org.restler.integration.springdata.Pet
import org.restler.integration.springdata.PetsRepository
import org.restler.spring.data.SpringDataSupport
import spock.lang.Specification

class PerformanceTest extends Specification {
    Service serviceWithBasicAuth = new Restler("http://localhost:8080",
            new SpringDataSupport([PersonsRepository.class, PetsRepository.class], 1000)).
            httpBasicAuthentication("user", "password").
            build();

    PersonsRepository personRepository = serviceWithBasicAuth.produceClient(PersonsRepository.class)
    PetsRepository petRepository = serviceWithBasicAuth.produceClient(PetsRepository.class)

    def "create persons"() {
        for(int i = 0; i < 1000; ++i) {
            personRepository.save(new Person(i, "Person number " + i))
        }
    }

    def "create pets"() {
        for(int i = 0; i < 1000; ++i) {
            petRepository.save(new Pet(i, "Pet number " + i, null))
        }
    }

    def "test"() {
        expect:
        "create persons"() == null
        "create pets"() == null

//        def persons = personRepository.findAll()
//
//        for(int i = 0; i < persons.size(); ++i) {
//            persons[i].setName(persons[i].getName() + " :)")
//            def pets = persons[i].getPets()
//            pets.add(new Pet(i, "", null))
//        }
    }

}
