package com.wini.leader_board_integration.service.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.wini.leader_board_integration.data.model.Country;
import com.wini.leader_board_integration.repository.CountryRepository;
import com.wini.leader_board_integration.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kamal on 2/5/2019.
 */
@Service
public class CountryServiceImpl implements CountryService {
    private static final Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.service.impl.CountryServiceImpl.class);
    private CountryRepository countryRepository;
    private final File geoIP2Database;
    private final DatabaseReader reader;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, @Value("${geodb.path}") final String filePath) throws IOException {
        this.countryRepository = countryRepository;
        geoIP2Database = new File(filePath);
        reader = new DatabaseReader.Builder(geoIP2Database).build();
    }

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country findOne(String id) {
        Optional<Country> country = countryRepository.findById(id);
        if (country.isPresent()) {
            return country.get();
        }
        return null;
    }

    @Override
    public Country findByCountryCode(String countryCode) {
        return countryRepository.findByCountryCode(countryCode);
    }

    @Override
    public Country findByIp(InetAddress inetAddress) {
        try {
            CountryResponse countryResponse = reader.country(inetAddress);
            Country country = findByCountryCode(countryResponse.getCountry().getIsoCode());
            return country;
        } catch (IOException e) {
            return null;
        } catch (GeoIp2Exception e) {
            return null;
        }
    }

    @Override
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }


}
