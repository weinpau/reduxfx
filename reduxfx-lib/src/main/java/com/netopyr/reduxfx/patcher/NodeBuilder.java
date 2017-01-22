package com.netopyr.reduxfx.patcher;

import com.netopyr.reduxfx.patcher.property.Accessor;
import com.netopyr.reduxfx.patcher.property.Accessors;
import com.netopyr.reduxfx.vscenegraph.VNode;
import com.netopyr.reduxfx.vscenegraph.event.VEventHandler;
import com.netopyr.reduxfx.vscenegraph.event.VEventType;
import com.netopyr.reduxfx.vscenegraph.property.VProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javaslang.Tuple2;
import javaslang.collection.Map;
import javaslang.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static com.netopyr.reduxfx.patcher.NodeUtilities.getChildren;

public class NodeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(NodeBuilder.class);

    private final Accessors accessors;
    private final Consumer<Object> dispatcher;

    NodeBuilder(Consumer<Object> dispatcher, Accessors accessors) {
        this.dispatcher = dispatcher;
        this.accessors = accessors;
    }

    @SuppressWarnings("unchecked")
    public Option<Node> create(VNode vNode) {
        try {
            final Class<? extends Node> nodeClass = vNode.getNodeClass();
            final Node node = nodeClass.newInstance();
            return Option.of(node);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Unable to create node", e);
            return Option.none();
        }
    }

    @SuppressWarnings("unchecked")
    public void init(Node node, VNode vNode) {
        setProperties(node, vNode.getProperties());
        setEventHandlers(node, vNode.getEventHandlers());

        if (vNode.getChildren().nonEmpty()) {
            final Option<java.util.List<Node>> children = getChildren(node);
            if (children.isEmpty()) {
                LOG.error("VNode has children defined, but is neither a Group nor a Pane: {}", vNode);
                return;
            }

            vNode.getChildren().forEach(vChild -> {
                final Option<Node> child = create(vChild);
                if (child.isDefined()) {
                    children.get().add(child.get());
                    init(child.get(), vChild);
                }
            });
        }


    }

    @SuppressWarnings("unchecked")
    private void setProperties(Node node, Map<String, VProperty> properties) {
        for (final Tuple2<String, VProperty> entry : properties) {
            final Option<Accessor> accessor = accessors.getAccessor(node, entry._1);
            if (accessor.isDefined()) {
                accessor.get().set(node, entry._1, entry._2);
            } else {
                LOG.warn("Accessor not found for property {} in class {}", entry._1, node.getClass());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setEventHandlers(Node node, Map<VEventType, VEventHandler> eventHandlers) {
        for (final Tuple2<VEventType, VEventHandler> entry : eventHandlers) {
            final Option<MethodHandle> setter = getEventSetter(node.getClass(), entry._1);
            if (setter.isDefined()) {
                try {
                    final EventHandler<? extends Event> eventHandler = e -> {
                        final Object action = entry._2.onChange(e);
                        if (action != null) {
                            dispatcher.accept(action);
                        }
                    };
                    setter.get().invoke(node, eventHandler);
                } catch (Throwable throwable) {
                    LOG.error("Unable to set JavaFX EventHandler " + entry._1 + " for class " + node.getClass(), throwable);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    void updateProperties(Node node, Map<String, VProperty> properties) {
        for (final Tuple2<String, VProperty> entry : properties) {
            final Option<Accessor> accessor = accessors.getAccessor(node, entry._1);
            if (accessor.isDefined()) {
                accessor.get().set(node, entry._1, entry._2);
            } else {
                LOG.warn("Accessor not found for property {} in class {}", entry._1, node.getClass());
            }
        }
    }

    @SuppressWarnings("unchecked")
    void updateEventHandlers(Node node, Map<VEventType, Option<VEventHandler>> eventHandlers) {
        for (final Tuple2<VEventType, Option<VEventHandler>> entry : eventHandlers) {
            final Option<MethodHandle> setter = getEventSetter(node.getClass(), entry._1);
            if (setter.isDefined()) {
                try {
                    final EventHandler<Event> eventHandler =
                            entry._2.map(
                                    vEventHandler ->
                                            (EventHandler<Event>) event -> {
                                                final Object action = vEventHandler.onChange(event);
                                                if (action != null) {
                                                    dispatcher.accept(action);
                                                }
                                            }
                            ).getOrElse((EventHandler) null);
                    setter.get().invoke(node, eventHandler);
                } catch (Throwable throwable) {
                    LOG.error("Unable to set JavaFX EventHandler " + entry._1() + " for class " + node.getClass(), throwable);
                }
            }
        }
    }

    private static Option<MethodHandle> getEventSetter(Class<? extends Node> clazz, VEventType eventType) {
        // TODO: Cache the getter
        final String eventName = eventType.getName();
        final String setterName = "setOn" + eventName.substring(0, 1).toUpperCase() + eventName.substring(1);

        Method method = null;
        try {
            method = clazz.getMethod(setterName, EventHandler.class);
        } catch (NoSuchMethodException e) {
            // ignore
        }

        if (method == null) {
            LOG.error("Unable to find setter for EventHandler {} in class {}", eventName, clazz);
            return Option.none();
        }

        try {
            final MethodHandle methodHandle = MethodHandles.publicLookup().unreflect(method);
            return Option.of(methodHandle);
        } catch (IllegalAccessException e) {
            LOG.error("Setter for EventHandler {} in class {} is not accessible", eventName, clazz);
            return Option.none();
        }
    }
}
