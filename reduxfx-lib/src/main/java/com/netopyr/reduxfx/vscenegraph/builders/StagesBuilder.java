package com.netopyr.reduxfx.vscenegraph.builders;

import com.netopyr.reduxfx.vscenegraph.Stages;
import com.netopyr.reduxfx.vscenegraph.VNode;
import javaslang.collection.Array;
import javaslang.collection.HashMap;

public class StagesBuilder extends VNode {

    public StagesBuilder(Array<VNode> children) {
        super(Stages.class, children, HashMap.empty(), HashMap.empty(), HashMap.empty());
    }


    public final StagesBuilder children(StageBuilder<?>... stages) {
        final Array<VNode> children = stages == null? Array.empty() : Array.of(stages);
        return new StagesBuilder(children);
    }

    public final StagesBuilder children(Iterable<StageBuilder<?>> stages) {
        final Array<VNode> children = stages == null? Array.empty() : Array.ofAll(stages);
        return new StagesBuilder(children);
    }

}
