package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.Country;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by kamal on 2/5/2019.
 */
public interface CountryRepository extends MongoRepository<Country, String> {

    Country findByCountryCode(String countryCode);

}
