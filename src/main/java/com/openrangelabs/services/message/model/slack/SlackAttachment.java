package com.openrangelabs.services.message.model.slack;

import lombok.Data;

import java.util.List;

@Data
public class SlackAttachment {
    String color;
    String pretext;
    String title;
    String text;
    List<SlackField> fields;

    public SlackAttachment(String color, String pretext, String title, String text, List<SlackField> fields) {
        this.color = color;
        this.pretext = pretext;
        this.title = title;
        this.text =text;
        this.fields =fields;
    }
}
