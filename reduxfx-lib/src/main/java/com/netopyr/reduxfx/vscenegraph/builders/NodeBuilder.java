package com.netopyr.reduxfx.vscenegraph.builders;

import com.netopyr.reduxfx.vscenegraph.VNode;
import com.netopyr.reduxfx.vscenegraph.event.VEventHandler;
import com.netopyr.reduxfx.vscenegraph.event.VEventType;
import com.netopyr.reduxfx.vscenegraph.property.VChangeListener;
import com.netopyr.reduxfx.vscenegraph.property.VInvalidationListener;
import com.netopyr.reduxfx.vscenegraph.property.VProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javaslang.collection.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.netopyr.reduxfx.vscenegraph.event.VEventType.MOUSE_CLICKED;

public class NodeBuilder<BUILDER extends NodeBuilder<BUILDER>> extends VNode {

    private static final String ID = "id";
    private static final String STYLE_CLASS = "styleClass";
    private static final String VISIBLE = "visible";
    private static final String HGROW = "hgrow";
    private static final String TOP_ANCHOR = "topAnchor";
    private static final String BOTTOM_ANCHOR = "bottomAnchor";
    private static final String LEFT_ANCHOR = "leftAnchor";
    private static final String RIGHT_ANCHOR = "rightAnchor";
    private static final String HOVER = "hover";
    private static final String FOCUSED = "focused";
    private static final String MARGIN = "margin";
    private static final String OPACITY = "opacity";
    private static final String DISABLE = "disable";
    private static final String STYLE = "style";

    public NodeBuilder(Class<? extends Node> nodeClass,
                       Map<String, VProperty> properties,
                       Map<VEventType, VEventHandler> eventHandlers) {
        super(nodeClass, properties, eventHandlers);
    }


    @SuppressWarnings("unchecked")
    protected BUILDER create(Map<String, VProperty> properties, Map<VEventType, VEventHandler> eventHandlers) {
        return (BUILDER) new NodeBuilder(getNodeClass(), properties, eventHandlers);
    }

    protected Object getTypeKey() {
        return getNodeClass();
    }


    public BUILDER disable(boolean value) {
        return property(DISABLE, value);
    }

    public BUILDER focused(boolean value, VChangeListener<? super Boolean> listener) {
        return property(FOCUSED, value, listener);
    }

    public BUILDER focused(boolean value) {
        return property(FOCUSED, value);
    }

    public BUILDER hover(VChangeListener<? super Boolean> listener) {
        return property(HOVER, listener);
    }

    public BUILDER id(String value) {
        return property(ID, value);
    }

    public BUILDER opacity(double value) {
        return property(OPACITY, value);
    }

    public BUILDER style(String value) {
        return property(STYLE, value);
    }

    public BUILDER styleClass(String... value) {
        return property(STYLE_CLASS, value == null ? FXCollections.emptyObservableList() : FXCollections.observableArrayList(value));
    }

    public BUILDER visible(boolean value) {
        return property(VISIBLE, value);
    }


    public BUILDER onMouseClicked(VEventHandler<MouseEvent> eventHandler) {
        return onEvent(MOUSE_CLICKED, eventHandler);
    }


    public BUILDER hgrow(Priority value) {
        return property(HGROW, value);
    }

    public BUILDER bottomAnchor(double value) {
        return property(BOTTOM_ANCHOR, value);
    }

    public BUILDER leftAnchor(double value) {
        return property(LEFT_ANCHOR, value);
    }

    public BUILDER rightAnchor(double value) {
        return property(RIGHT_ANCHOR, value);
    }

    public BUILDER topAnchor(double value) {
        return property(TOP_ANCHOR, value);
    }

    public BUILDER margin(double top, double rightLeft, double bottom) {
        return property(MARGIN, new Insets(top, rightLeft, bottom, rightLeft));
    }

    public BUILDER margin(double topBottom, double rightLeft) {
        return property(MARGIN, new Insets(topBottom, rightLeft, topBottom, rightLeft));
    }

    public BUILDER margin(double value) {
        return property(MARGIN, new Insets(value, value, value, value));
    }


    public <TYPE> BUILDER property(String name, TYPE value, VChangeListener<? super TYPE> changeListener, VInvalidationListener invalidationListener) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(value, changeListener, invalidationListener)),
                getEventHandlers()
        );
    }

    public <TYPE> BUILDER property(String name, TYPE value, VChangeListener<? super TYPE> changeListener) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(value, changeListener)),
                getEventHandlers()
        );
    }

    public BUILDER property(String name, Object value, VInvalidationListener invalidationListener) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(value, invalidationListener)),
                getEventHandlers()
        );
    }

    public BUILDER property(String name, Object value) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(value)),
                getEventHandlers()
        );
    }

    public BUILDER property(String name, VChangeListener<?> changeListener, VInvalidationListener invalidationListener) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(changeListener, invalidationListener)),
                getEventHandlers()
        );
    }

    public BUILDER property(String name, VChangeListener<?> changeListener) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(changeListener)),
                getEventHandlers()
        );
    }

    public BUILDER property(String name, VInvalidationListener invalidationListener) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property(invalidationListener)),
                getEventHandlers()
        );
    }

    public BUILDER property(String name) {
        return Factory.node(
                this,
                getProperties().put(name, Factory.property()),
                getEventHandlers()
        );
    }

    public <EVENT extends Event> BUILDER onEvent(VEventType type, VEventHandler<EVENT> eventHandler) {
        return Factory.node(
                this,
                getProperties(),
                getEventHandlers().put(type, eventHandler)
        );
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }
}
