package com.technochord.ui.chat.view;

import com.technochord.ui.chat.service.QueryService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
@Route("")
public class ChatView extends VerticalLayout{
    private TextArea inputArea;
    private TextArea responseArea;
    private Button submitButton;

    private QueryService queryService;

    public ChatView(final QueryService queryService) {
        this.queryService = queryService;

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

        // Response text area
        responseArea = new TextArea("Response");
        responseArea.setPlaceholder("Response will appear here...");
        responseArea.setWidth("600px");
        responseArea.setHeight("500px");
        responseArea.setReadOnly(true);

        // Layout configuration
        add(title, inputArea, submitButton, responseArea);
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

        // Process the input - replace this with your actual logic
        String response = processInput(query);

        // Display the response
        responseArea.setValue(response);

        Notification.show("Processing complete!", 2000,
                Notification.Position.BOTTOM_END);
    }

    private String processInput(String query) {
        String processed = queryService.process(query);
        return  processed;
    }

}
