package com.openrangelabs.services.signing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentTemplateTextItems {

    String color = "000000";

    @JsonProperty("data_type")
    String dataType;

    @JsonProperty("data")
    String data;

    @JsonProperty("page_number")
    String pageNumber;

    @JsonProperty("x")
    String x;

    @JsonProperty("y")
    String y;

    @JsonProperty("line_height")
    String lineHeight;

    @JsonProperty("size")
    String size;

    @JsonProperty("font")
    String font;

    @JsonProperty("bold")
    Boolean bold;

    @JsonProperty("italic")
    Boolean italic;

    @JsonProperty("underline")
    Boolean underline;

    public void setDataType(String dataType) {
        if (dataType.contains("pre_auth")) {
            this.setColor("888888");
        }
        this.dataType = dataType;
    }
}
