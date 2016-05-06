package org.restler.integration

import org.restler.Restler
import org.restler.Service
import org.restler.integration.springdata.Address
import org.restler.integration.springdata.AddressRepository
import org.restler.integration.springdata.Person
import org.restler.integration.springdata.PersonsRepository
import org.restler.integration.springdata.Pet
import org.restler.integration.springdata.PetsRepository
import org.restler.spring.data.SpringDataSupport
import spock.lang.Specification

class PerformanceTest extends Specification {
    Service serviceWithBasicAuth = new Restler("http://192.168.0.37:8080",
            new SpringDataSupport([PersonsRepository.class, PetsRepository.class, AddressRepository.class], 1000)).
//            httpBasicAuthentication("user", "password").
            build();

    PersonsRepository personRepository = serviceWithBasicAuth.produceClient(PersonsRepository.class)
    PetsRepository petRepository = serviceWithBasicAuth.produceClient(PetsRepository.class)
    AddressRepository addressRepository = serviceWithBasicAuth.produceClient(AddressRepository.class)

    static def persons = new ArrayList<Person>()
    static def pets = new ArrayList<Pet>()
    static def addresses = new ArrayList<Address>()

    def "create persons"() {
        expect:
        for(int i = 0; i < 1000; ++i) {
            personRepository.save(new Person(i, "Person number " + i))
        }
        true
    }

    def "create pets"() {
        expect:
        for(int i = 0; i < 1000; ++i) {
            petRepository.save(new Pet(i, "Pet number " + i, null))
        }
        true
    }

    def "create addresses"() {
        expect:
        for(int i = 0; i < 1000; ++i) {
            for(int j = 0; j < 10; j++) {
                addressRepository.save(new Address(i * 10 + j, "Address number " + (i * 10 + j), null))
            }

        }
    }

    def "get persons"() {
        expect:
        for(int i = 0; i < 1000; ++i) {
            persons.add(personRepository.findOne(new Long(i)))
        }
        true
    }

    def "get pets"() {
        expect:
        for(int i = 0; i < 1000; ++i) {
            pets.add(petRepository.findOne(new Long(i)))
        }
        true
    }

    def "get addresses"() {
        expect:
        for(int i = 0; i < 10000; ++i) {
            addresses.add(addressRepository.findOne(new Long(i)))
        }
    }

    def "add pets and addresses to persons"() {
        expect:
        for(int i = 0; i < 1000; ++i) {
            def personPets = persons[i].getPets()
            def personAddresses = persons[i].getAddresses()
            personPets.add(pets[i])
            for(int j = 0; j < 10; j++) {
                personAddresses.add(addresses[i * 10 + j])
            }
            personRepository.save(persons[i])
        }

        true
    }

    def "change addresses"() {
        expect:
        for(Person person : persons) {
            def addresses = person.getAddresses()

            for(Address address : addresses) {
                address.setName(address.getName() + " " + person.getName())
                addressRepository.save(address)
            }
        }

        true
    }

}
