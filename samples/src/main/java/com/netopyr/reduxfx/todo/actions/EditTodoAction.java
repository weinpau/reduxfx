package com.netopyr.reduxfx.todo.actions;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * An {@code EditTodoAction} is passed to the {@link com.netopyr.reduxfx.todo.updater.Updater} when the {@code text} of
 * a {@link com.netopyr.reduxfx.todo.state.TodoEntry} needs to be changed
 */
public final class EditTodoAction implements Action {

    private final int id;
    private final String text;

    EditTodoAction(int id, String text) {
        Objects.requireNonNull(text, "The parameter 'text' must not be null");
        this.id = id;
        this.text = text;
    }

    /**
     * This is the getter for the {@code id} of the {@link com.netopyr.reduxfx.todo.state.TodoEntry} which
     * {@code text} needs to be changed.
     *
     * @return the {@code id}
     */
    public int getId() {
        return id;
    }

    /**
     * This is the getter of the new text
     *
     * @return the new text
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("text", text)
                .toString();
    }
}
