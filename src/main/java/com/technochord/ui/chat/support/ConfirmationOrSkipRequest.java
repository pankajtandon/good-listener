package com.technochord.ui.chat.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ConfirmationOrSkipRequest {
    private String conversationId;
    private boolean approved;
    private String feedback;
    private String modelName;
}
