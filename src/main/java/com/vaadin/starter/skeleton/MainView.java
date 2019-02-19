package com.vaadin.starter.skeleton;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
@PWA(name = "Project Base for Vaadin Flow", shortName = "Project Base")
public class MainView extends VerticalLayout {

    String[] steps = new String[]{
            "Filter out an item on page 2 or later (Item 51-200), do not scroll to the value",
            "Click reset, observe that the combo box gets it value set to null"
    };

    Text message;
    List<String> items;
    Data model;
    Binder<Data> binder;
    ComboBox<String> comboBox;

    class Data {
        String item;

        public String getItem() {
            return item;
        }

        public void setItem(String value) {
            item = value;
        }
    }


    public MainView() {
        generateItems();
        model = new Data();
        binder = new Binder<>();
        comboBox = new ComboBox<>("My combobox");
        comboBox.setDataProvider(DataProvider.fromFilteringCallbacks(this::fetchCallback, this::countCallback));
        binder.forField(comboBox).asRequired("Must have a value").bind(Data::getItem, Data::setItem);
        binder.setBean(model);
        message = new Text("");
        add(reproductionSteps());
        add(comboBox);
        add(message);
        add(new Button("Read server value", this::onValidateClick));
        add(new Button("Reset bean", this::onResetClick));
    }

    private Component reproductionSteps() {
        OrderedList ol = new OrderedList();
        for (String step : steps) {
            ol.add(new ListItem(step));
        }
        return ol;
    }

    private void onResetClick(ClickEvent<Button> buttonClickEvent) {
        binder.setBean(model);
    }

    private void onValidateClick(ClickEvent<Button> buttonClickEvent) {
        updateMessageText();
    }

    private void updateMessageText() {
        message.setText(String.format("Model has value %s, combo box has value %s", model.getItem(), comboBox.getValue()));
    }

    private int countCallback(Query<String, String> query) {
        return (int) filterItems(query.getFilter()).count();
    }

    private Stream<String> filterItems(Optional<String> filter) {
        String filterString = filter.orElse("").toLowerCase();
        return items.stream().filter(i -> i.toLowerCase().startsWith(filterString));
    }

    private Stream<String> fetchCallback(Query<String, String> query) {
        return filterItems(query.getFilter()).skip(query.getOffset()).limit(query.getLimit());
    }

    private void generateItems() {
        items = new ArrayList<>(200);
        for (int i = 0; i < 200; ++i) {
            items.add(String.format("Item %d", i+1));
        }
    }
}
