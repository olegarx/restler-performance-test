package org.restler.integration.springdata;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = true, path = "addresses")
public interface AddressRepository extends CrudRepository<Address, Long> {
    Address findById(@Param("id") Long id);
}
