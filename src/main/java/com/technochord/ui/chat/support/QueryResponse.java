package com.technochord.ui.chat.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QueryResponse {
    String response;
    boolean needsConfirmation;
    String conversationId;
    String error;
}
