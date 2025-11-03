package com.technochord.ui.chat.view;

import com.technochord.ui.chat.service.QueryService;
import com.technochord.ui.chat.support.QueryResponse;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("")
public class ChatView extends VerticalLayout {
    private final String CONVERSATION_ID = "CONVERSATION_ID";
    private TextArea inputArea;
    private TextArea responseArea;
    private TextArea confirmArea;
    private Button submitButton;
    private Button confirmButton;
    private Button skipButton;
    private ComboBox<Integer> comboBox;
    private Html topKLabel;

    private QueryService queryService;

    public ChatView(final QueryService queryService) {
        this.queryService = queryService;
        //topKLabel = new Html();
        comboBox = new ComboBox<>("Select a number that limits the number of tools \n that are semantically similar to your query \n and presented to the LLM: ");
        comboBox.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9);
        comboBox.setAllowCustomValue(true);
        comboBox.setValue(100);
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

        // Page title
        H1 title = new H1("Good Listener");

        // Input text area
        inputArea = new TextArea("Query");
        inputArea.setPlaceholder("Enter your question here...");
        inputArea.setWidth("600px");
        inputArea.setHeight("150px");


        // Submit button
        submitButton = new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(e -> handleSubmit());
        HorizontalLayout topKSubmitLayout = new HorizontalLayout(comboBox, submitButton);

        // Confirm button
        confirmButton = new Button("Confirm");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.addClickListener(e -> handleConfirmOrSkip(true));

        skipButton = new Button("Skip");
        skipButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        skipButton.addClickListener(e -> handleConfirmOrSkip(false));

        // Confirmation text area
        confirmArea = new TextArea("Tool Use Confirmation");
        confirmArea.setPlaceholder("Tool confirmations will appear here...");
        confirmArea.setWidth("600px");
        confirmArea.setHeight("200px");
        confirmArea.setReadOnly(true);

        // Response text area
        responseArea = new TextArea("Response");
        responseArea.setPlaceholder("Response will appear here...");
        responseArea.setWidth("600px");
        responseArea.setHeight("500px");
        responseArea.setReadOnly(true);

        HorizontalLayout confirmOrSkipLayout = new HorizontalLayout(confirmButton, skipButton);
        // Layout configuration
        add(title, inputArea, comboBox, topKSubmitLayout, confirmArea, confirmOrSkipLayout, responseArea);
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

    private void handleSubmit() {
        String query = inputArea.getValue();

        if (query == null || query.trim().isEmpty()) {
            Notification.show("Please enter some text before submitting", 3000,
                    Notification.Position.MIDDLE);
            return;
        }

        QueryResponse queryResponse = queryService.processInput(query, Integer.toString(comboBox.getValue()));
        if (queryResponse.isNeedsConfirmation()) {
            VaadinSession.getCurrent().setAttribute(CONVERSATION_ID, queryResponse.getConversationId());
            confirmArea.setValue(queryResponse.getResponse());
        } else {
            // Display the final response
            responseArea.setValue(queryResponse.getResponse());
            Notification.show("Processing complete!", 2000,
                    Notification.Position.BOTTOM_END);
        }
    }

    private void handleConfirmOrSkip(boolean approved) {
        String query = inputArea.getValue();
        // Process the confirmation
        String retrievedConversationId = (String)VaadinSession.getCurrent().getAttribute(CONVERSATION_ID);
        QueryResponse queryResponse = queryService.processConfirmOrSkip(retrievedConversationId, approved, null);

        if (queryResponse.isNeedsConfirmation()) {
            confirmArea.setValue(queryResponse.getResponse());
        } else {
            // Display the final response
            responseArea.setValue(queryResponse.getResponse());
            Notification.show("Processing complete!", 2000,
                    Notification.Position.BOTTOM_END);
        }
    }
}
