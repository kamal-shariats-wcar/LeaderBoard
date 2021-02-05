package com.wini.leader_board_integration.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by kamal on 2/5/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "country")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Country extends BaseDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String countryName;
    @Indexed(unique = true)
    private String countryCode;
    private String imageUrl;
    private String dialCode;
    private String continentName;

    public Country(String id, String countryName, String countryCode, String imageUrl, String continentName) {
        this.id = id;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.imageUrl = imageUrl;
        this.continentName = continentName;
    }

    public Country(String countryName, String countryCode, String imageUrl, String dialCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.imageUrl = imageUrl;
        this.dialCode = dialCode;
    }

    public com.wini.leader_board_integration.data.model.Country addBaserPath(String basePath) {
        this.setImageUrl(basePath + this.getImageUrl());
        return this;
    }
}
