package com.technochord.ui.chat.view;

import com.technochord.ui.chat.service.QueryService;
import com.technochord.ui.chat.support.QueryResponse;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Route("")
public class ChatView extends VerticalLayout {
    private final String CONVERSATION_ID = "CONVERSATION_ID";
    private TextArea inputArea;
    private Markdown responseArea;
    private TextArea relevantToolListArea;
    private TextArea toolListArea;
    private TextArea confirmArea;
    private Button submitButton;
    private Button confirmButton;
    private Button skipButton;
    private VerticalLayout inputLayout;
    private VerticalLayout confirmationLayout;
    private QueryService queryService;

    public ChatView(final QueryService queryService) {
        this.queryService = queryService;
        ComboBox<Integer> toolLimitComboBox = buildToolLimitComboBox();
        ComboBox<String> modelNameComboBox = buildModelNameComboBox();

        // Page title
        H1 title = new H1("Good Listener");

        inputLayout = new VerticalLayout();
        inputLayout.getStyle()
                .set("border", "2px solid #2196F3")
                .set("border-radius", "8px");

        // Input text area
        inputArea = new TextArea();
        inputArea.setPlaceholder("Enter your question here...");
        inputArea.setWidth("100%");
        inputArea.setHeight("150px");


        // Submit button
        submitButton = new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(e -> handleSubmit(toolLimitComboBox, modelNameComboBox));
        HorizontalLayout topKSubmitLayout = new HorizontalLayout(toolLimitComboBox, modelNameComboBox, submitButton);

        inputLayout.add(inputArea, topKSubmitLayout);
        inputLayout.setWidth("80%");

        confirmationLayout = new VerticalLayout();
        confirmationLayout.getStyle()
                .set("border", "2px solid #2196F3")
                .set("border-radius", "8px");
        // Confirm button
        confirmButton = new Button("Confirm");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.addClickListener(e -> handleConfirmOrSkip(true, modelNameComboBox));

        skipButton = new Button("Skip");
        skipButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        skipButton.addClickListener(e -> handleConfirmOrSkip(false, modelNameComboBox));

        // ToolList text area
        relevantToolListArea = new TextArea("Tools who's metadata is similar to the query (using similarity search against a vector database, aka RAG):");
        //relevantToolListArea.setPlaceholder("Tools names that can potentially be used to answer the query will appear here...");
        relevantToolListArea.setWidth("100%");
        relevantToolListArea.setHeight("100px");
        relevantToolListArea.setReadOnly(true);


        // ToolList text area
        toolListArea = new TextArea("Tools and their arguments that need to be executed (as determined by the LLM) after confirmation:");
        //toolListArea.setValue("Tools and their arguments that are going to be processed (only after confirmation) will appear here...");
        toolListArea.setWidth("100%");
        toolListArea.setHeight("200px");
        toolListArea.setReadOnly(true);

        // Confirmation text area
        confirmArea = new TextArea("Please confirm tool usage:");
        //confirmArea.setValue("Tool confirmations will appear here...");
        confirmArea.setWidth("100%");
        confirmArea.setHeight("200px");
        confirmArea.setReadOnly(true);
        HorizontalLayout confirmOrSkipLayout = new HorizontalLayout(confirmButton, skipButton);
        confirmationLayout.add(relevantToolListArea, toolListArea, confirmArea, confirmOrSkipLayout);
        confirmationLayout.setWidth("80%");

        // Response text area
        responseArea = new Markdown("Planner response will appear here...");
        responseArea.setHeight("100%");
        responseArea.getStyle()
                .set("border", "2px solid #2196F3")
                .set("border-radius", "8px");
        responseArea.setWidth("80%");

        // Layout configuration
        add(title, inputLayout, confirmationLayout, responseArea);
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

    private ComboBox buildToolLimitComboBox() {
        ComboBox<Integer> comboBox = new ComboBox<>("Tool Limit: ");
        comboBox.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9);
        comboBox.setAllowCustomValue(true);
        comboBox.setValue(4);
        comboBox.addCustomValueSetListener(event -> {
            try {
                Integer customValue = Integer.parseInt(event.getDetail());
                try {
                    comboBox.setValue(Integer.parseInt(event.getDetail()));
                } catch (NumberFormatException e) {
                    Notification.show("Please enter a valid number");
                    comboBox.clear();
                }
                comboBox.setValue(customValue);
            } catch (NumberFormatException e) {
                Notification.show("Invalid number", 3000,
                        Notification.Position.MIDDLE);
            }
        });
        return comboBox;
    }

    private ComboBox buildModelNameComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Model Name: ");
        comboBox.setItems("gpt-5-nano", "gpt-5-mini", "gpt-4o-2024-11-20", "gpt-4-turbo", "claude-haiku-4-5", "claude-sonnet-4-5");
        comboBox.setAllowCustomValue(true);
        comboBox.setValue("gpt-5-nano");
        comboBox.addCustomValueSetListener(event -> {
                String customValue = event.getDetail();
                try {
                    comboBox.setValue(event.getDetail());
                } catch (Exception e) {
                    Notification.show("Please enter a valid string");
                    comboBox.clear();
                }
                comboBox.setValue(customValue);
        });
        return comboBox;
    }

    private void handleSubmit(ComboBox<Integer> toolLimitComboBox, ComboBox<String> modelNameComboBox) {
        String query = inputArea.getValue();

        //TODO: This won't work because Vaadin renders after server call returns.
        // Implement later using @Push and UI.access
        relevantToolListArea.clear();
        toolListArea.clear();
        confirmArea.clear();
        responseArea.setContent(".");

        if (query == null || query.trim().isEmpty()) {
            Notification.show("Please enter some text before submitting", 3000,
                    Notification.Position.MIDDLE);
            return;
        }

        QueryResponse queryResponse = queryService.processInput(query, Integer.toString(toolLimitComboBox.getValue()), modelNameComboBox.getValue());
        if (queryResponse.isNeedsConfirmation()) {
            List<String> relevantToolList = queryResponse.getRelevantToolList();
            if (relevantToolList != null) {
                StringBuffer stringBuffer = new StringBuffer();
                AtomicReference<Integer> count = new AtomicReference<>(1);
                relevantToolList.stream().forEach(s -> {
                    stringBuffer.append("Tool " + count.get() + ": " + s + "\n");
                    count.set(count.get() + 1);
                });
                relevantToolListArea.setValue(stringBuffer.toString());
            }
            VaadinSession.getCurrent().setAttribute(CONVERSATION_ID, queryResponse.getConversationId());
            List<QueryResponse.ToolCall> toolCallList = queryResponse.getToolCallList();
            if (toolCallList != null) {
                StringBuffer stringBuffer = new StringBuffer();
                AtomicReference<Integer> count = new AtomicReference<>(1);
                toolCallList.stream().forEach(tc -> {
                    stringBuffer.append("Invocation " + count.get() + ": " + tc.name() + ", " + tc.arguments() + "\n");
                    count.set(count.get() + 1);
                });
                toolListArea.setValue(stringBuffer.toString());
            }
            confirmArea.setValue(queryResponse.getResponse());
        } else {
            // Display the final response
            responseArea.setContent(queryResponse.getResponse());
            Notification.show("Processing complete!", 2000,
                    Notification.Position.BOTTOM_END);
        }
    }

    private void handleConfirmOrSkip(boolean approved, ComboBox<String> modelNameComboBox) {
        String query = inputArea.getValue();
        // Process the confirmation
        String retrievedConversationId = (String)VaadinSession.getCurrent().getAttribute(CONVERSATION_ID);
        QueryResponse queryResponse = queryService.processConfirmOrSkip(retrievedConversationId, approved, null, modelNameComboBox.getValue());

        if (queryResponse.isNeedsConfirmation()) {
            confirmArea.setValue(queryResponse.getResponse());
        } else {
            // Display the final response
            responseArea.setContent(queryResponse.getResponse());
            Notification.show("Processing complete!", 2000,
                    Notification.Position.BOTTOM_END);
        }
    }
}
