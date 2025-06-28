package com.openrangelabs.services.message.model.slack;

import lombok.Data;

import java.util.List;

@Data
public class SlackErrorAlertRequest {
    String channel;
    String username;
    String icon_url;
    List<SlackAttachment> attachments;
}
