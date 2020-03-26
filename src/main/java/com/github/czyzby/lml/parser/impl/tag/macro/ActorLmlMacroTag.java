package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.MockLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Expanded evaluate macro. Instead of just finding and executing a method, it will expect that the method returns an
 * instance of {@link Actor} that should be appended. Returned actor will be A) added as a child to the tag in which the
 * macro is invoked, B) added to the result actors collection if the macro is not in a tag. Evaluation works similarly:
 * first argument is the method ID; if there is text between macro tags, method consuming string is invoked with the raw
 * data between tags; otherwise, method consuming parent actor is invoked. Second macro attribute becomes actor's ID.
 * For example: <blockquote>
 *
 * <pre>
 * &lt;table&gt;&lt;:actor methodName/&gt;&lt;/table&gt;
 * </pre>
 *
 * </blockquote> This will look for a method mapped to "methodName" and invoke it with the Table (parent's actor) as its
 * argument. The returned actor will be added to the table.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:actor methodId actorId&gt;Method argument&lt;/:actor&gt;
 * </pre>
 *
 * </blockquote>This will look for a method mapped to "methodId" and invoke it with a string parameter:
 * "Method argument" (which was between macro tags). The actor's ID will be set as "actorId", as specified by second
 * macro attribute. Since this macro is not in another tag, this actor will be added directly to the result collection
 * and will not be appended by any parent.
 * <p>
 * This macro can be also used with named parameters: <blockquote>
 *
 * <pre>
 * &lt;:actor method="methodId" id="actorId"&gt;Method argument&lt;/:actor&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class ActorLmlMacroTag extends EvaluateLmlMacroTag {
    public ActorLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected void processMethodResult(final Object result) {
        if (result instanceof Actor) {
            final Actor actor = (Actor) result;
            addActorId(actor);
            final LmlTag parent = getParent();
            if (parent != null) {
                // Macro is inside another tag. Delegating child handling to the parent.
                parent.handleChild(new MockLmlTag(actor, parent));
            } else {
                // Macro is not in a tag. Adding actor to the result.
                getParser().addActor(actor);
            }
        } else {
            getParser().throwErrorIfStrict("Actor macro has to reference a method that returns a valid Actor object.");
        }
    }

    /** @param actor if the macro has a second attribute, it will set it as this actor's ID. */
    protected void addActorId(final Actor actor) {
        if (hasAssignmentArgumentName()) {
            final String id = getAssignmentArgumentName();
            LmlUtilities.setActorId(actor, id);
            getParser().getActorsMappedByIds().put(id, actor);
        }
    }
}
