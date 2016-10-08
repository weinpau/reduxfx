package com.netopyr.reduxfx.patcher.property;

import com.netopyr.reduxfx.vscenegraph.property.VProperty;
import com.netopyr.reduxfx.vscenegraph.property.VPropertyType;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collections;

import static java.lang.invoke.MethodType.methodType;

public class ListWithoutListenerAccessor<ACTION> implements PropertyAccessor<ObservableList, ACTION> {

    private final MethodHandle getter;

    public ListWithoutListenerAccessor(Class<? extends Node> clazz, VPropertyType propertyType) {
        this.getter = getGetter(clazz, propertyType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Node node, VProperty<ObservableList, ACTION> vProperty) {
        final ObservableList list;
        try {
            list = (ObservableList) getter.invoke(node);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Unable to read value of property " + vProperty.getType() + " from Node-class " + node.getClass(), throwable);
        }

        list.setAll(vProperty.getValue() == null? Collections.emptyList() : vProperty.getValue());
    }

    private MethodHandle getGetter(Class<? extends Node> clazz, VPropertyType propertyType) {
        final String propertyName = propertyType.getName();
        final String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

        try {
            return MethodHandles.publicLookup().findVirtual(clazz, getterName, methodType(ObservableList.class));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to find getter of property " + propertyName + " in class " + clazz, e);
        }
    }
}