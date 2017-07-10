package it.polimi.jasper.engine.reasoning.rulesys;

import it.polimi.jasper.engine.reasoning.TimeVaryingInfGraph;
import it.polimi.yasper.core.query.operators.s2r.WindowOperator;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.reasoner.BaseInfGraph;
import org.apache.jena.reasoner.Finder;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.TriplePattern;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 * Created by riccardo on 06/07/2017.
 */
public class BaseInfTVGraph extends BaseInfGraph implements TimeVaryingInfGraph {
    private long last_timestamp;
    private WindowOperator window;

    /**
     * Constructor
     *
     * @param data     the raw data file to be augmented with entailments
     * @param reasoner the engine, with associated tbox data, whose find interface
     */
    public BaseInfTVGraph(Graph data, Reasoner reasoner, long timestamp, WindowOperator w) {
        super(data, reasoner);
        this.last_timestamp = last_timestamp;
        this.window = w;
    }

    @Override
    public long getTimestamp() {
        return last_timestamp;
    }

    @Override
    public void setTimestamp(long ts) {
        this.last_timestamp = ts;
    }

    @Override
    public WindowOperator getWindowOperator() {
        return window;
    }

    @Override
    public void setWindowOperator(WindowOperator w) {
        this.window = w;
    }

    @Override
    public ExtendedIterator<Triple> findWithContinuation(TriplePattern pattern, Finder continuation) {
        return null;
    }

    @Override
    public Graph getSchemaGraph() {
        return null;
    }
}
