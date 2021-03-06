package it.polimi.yasper.core.operators.r2s;

import it.polimi.yasper.core.querying.result.SolutionMapping;

import java.util.stream.Stream;

/**
 * Created by riccardo on 07/07/2017.
 */
public interface RelationToStreamOperator<T> {

    T eval(SolutionMapping<T> sm, long ts);

    default Stream<T> eval(Stream<SolutionMapping<T>> sml, long ts) {
        return sml.map(sm -> eval(sm, ts));
    }
}
