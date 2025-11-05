package com.technochord.ui.chat.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class QueryResponse {
    String response;
    boolean needsConfirmation;
    String conversationId;
    String error;
    List<ToolCall> toolCallList;

    public static record ToolCall(String id, String type, String name, String arguments) {
    }
}
