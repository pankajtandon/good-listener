package com.technochord.ui.chat.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class QueryRequest {

    /**
     * The query sent to the LLM.
     */
    private String message;

    /**
     * This is an integer representing the top K functions that will be passed to the LLM.
     * The higher the number, the more function metadata will be sent to the LLM, resulting in
     * more tokens being consumed.
     */
    private String userSuppliedTopK;
}
