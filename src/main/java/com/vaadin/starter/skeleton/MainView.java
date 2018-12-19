package com.vaadin.starter.skeleton;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        add(new OrderedList(
                new ListItem("Change value to 'Invalid'"),
                new ListItem("Defocus the text field"),
                new ListItem("Observe that the validation error has dissapeared for A but not B nor C")
        ));

        ComboBox<String> comboBox1 = new ComboBox<>("Name");
        comboBox1.setItems("Valid", "Invalid");
        add(makeContainerWithRequired("A. Combo box w. validator and required", comboBox1));

        TextField textField = new TextField("Name");
        add(makeContainerWithRequired("B. Text field w. validator and required", textField));

        ComboBox<String> comboBox2 = new ComboBox<>("Name");
        comboBox2.setItems("Valid", "Invalid");
        add(makeContainerWithoutRequired("C. Combo box w. validator but not required", comboBox2));

    }

    public static class Data {
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String val) {
            value = val;
        }
    }

    private ValidationResult notChangedLater(String value, ValueContext context) {
        return "Invalid".equalsIgnoreCase(value) ? ValidationResult.error("It can't be 'Invalid'") : ValidationResult.ok();
    }

    private Component makeContainer(String header, AbstractField<?, String> component, boolean required) {
        Div container = new Div();
        Style style = container.getStyle();
        style.set("border", "1px solid black");
        style.set("padding", "15px");
        H4 heading = new H4(header);
        Div text = new Div(new Text("This field may not have the value 'Invalid'"));

        Binder<Data> binder = new Binder<>(Data.class);
        Binder.BindingBuilder<Data, String> bindingBuilder = binder.forField(component).withValidator(this::notChangedLater);
        Div state = new Div();
        component.addValueChangeListener((change) -> state.setText(String.format("Server value is '%s', and isValid == %b", change.getValue(), binder.isValid())));
        if (required) {
            bindingBuilder
                    .asRequired("You must have a value")
                    .bind(Data::getValue, Data::setValue);
        } else {
            bindingBuilder
                    .bind(Data::getValue, Data::setValue);
        }

        Data myData = new Data();
        binder.setBean(myData);

        container.add(heading, text, component, state);

        return container;
    }

    private Component makeContainerWithoutRequired(String header, AbstractField<?, String> component) {
        return makeContainer(header, component, false);
    }

    private Component makeContainerWithRequired(String header, AbstractField<?, String> component) {
        return makeContainer(header, component, true);
    }
}
