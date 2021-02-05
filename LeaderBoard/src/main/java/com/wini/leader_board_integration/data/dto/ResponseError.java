
package com.wini.leader_board_integration.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ResponseError {

    private String message;
    private String type;
    private Long code;
    @JsonProperty("error_subcode")
    private Long errorSubcode;
    @JsonProperty("fbtrace_id")
    private String fbtraceId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Long getErrorSubcode() {
        return errorSubcode;
    }

    public void setErrorSubcode(Long errorSubcode) {
        this.errorSubcode = errorSubcode;
    }

    public String getFbtraceId() {
        return fbtraceId;
    }

    public void setFbtraceId(String fbtraceId) {
        this.fbtraceId = fbtraceId;
    }
}
