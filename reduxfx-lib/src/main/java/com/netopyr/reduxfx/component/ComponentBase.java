package com.netopyr.reduxfx.component;

import com.netopyr.reduxfx.ReduxFX;
import com.netopyr.reduxfx.component.driver.ComponentDriver;
import com.netopyr.reduxfx.updater.Update;
import com.netopyr.reduxfx.vscenegraph.VNode;
import com.netopyr.reduxfx.vscenegraph.property.VChangeListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ComponentBase<STATE> extends ReduxFX<STATE> {

    private final ComponentDriver componentDriver = new ComponentDriver();

    public ComponentBase(
            Parent component,
            STATE initialState,
            BiFunction<STATE, Object, Update<STATE>> updater,
            Function<STATE, VNode> view
    ) {
        super(initialState, updater, view, component);
        component.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                start();
            } else {
                stop();
            }
        });

        registerDriver(componentDriver);
    }

    public ReadOnlyIntegerProperty createReadOnlyIntegerProperty(Object bean, String name) {
        return componentDriver.createReadOnlyIntegerProperty(bean, name);
    }

    public <T> ReadOnlyObjectProperty<T> createReadOnlyObjectProperty(Object bean, String name) {
        return componentDriver.createReadOnlyObjectProperty(bean, name);
    }


    public <T> ObjectProperty<T> createObjectProperty(Object bean, String name, VChangeListener<T> listener) {
        return componentDriver.createObjectProperty(bean, name, listener);
    }


    public <EVENT extends Event> ObjectProperty<EventHandler<EVENT>> createEventHandlerProperty(Object bean, String name) {
        return componentDriver.createEventHandlerProperty(bean, name);
    }
}
