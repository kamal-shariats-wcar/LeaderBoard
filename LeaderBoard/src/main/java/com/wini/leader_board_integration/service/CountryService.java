package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.model.Country;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;

/**
 * Created by kamal on 2/5/2019.
 */
public interface CountryService {
    @CachePut(value = "country",key = "#country.id")
    @CacheEvict(value = "countryByCountryCode",key = "#country.countryCode")
    Country save(Country country);
    @Cacheable(value = "country")
    Country findOne(String id);
    @Cacheable(value = "countryByCountryCode")
    Country findByCountryCode(String countryCode);

    Country findByIp(InetAddress inetAddress);

    String getIpAddress(HttpServletRequest request);

}
