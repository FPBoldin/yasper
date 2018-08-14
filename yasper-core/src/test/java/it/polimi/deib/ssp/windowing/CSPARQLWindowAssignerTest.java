package it.polimi.deib.ssp.windowing;

import it.polimi.deib.ssp.utils.StreamViewImpl;
import it.polimi.deib.ssp.utils.WritableStream;
import it.polimi.yasper.core.quering.TimeVarying;
import it.polimi.yasper.core.spe.report.Report;
import it.polimi.yasper.core.spe.report.ReportGrain;
import it.polimi.yasper.core.spe.report.ReportImpl;
import it.polimi.yasper.core.spe.report.strategies.OnWindowClose;
import it.polimi.yasper.core.spe.scope.Tick;
import it.polimi.yasper.core.spe.windowing.assigner.CSPARQLWindowAssigner;
import it.polimi.yasper.core.spe.windowing.assigner.WindowAssigner;
import it.polimi.yasper.core.utils.RDFUtils;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Triple;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class CSPARQLWindowAssignerTest {

    @Test
    public void test() {

        Report report = new ReportImpl();
        report.add(new OnWindowClose());

        Tick tick = Tick.TIME_DRIVEN;
        ReportGrain report_grain = ReportGrain.SINGLE;

        int scope = 0;


        //WINDOW DECLARATION
        WindowAssigner windowAssigner = new CSPARQLWindowAssigner(RDFUtils.createIRI("w1"), 2000, 2000, scope, 0);

        //ENGINE INTERNALS - HOW THE REPORTING POLICY, TICK AND REPORT GRAIN INFLUENCE THE RUNTIME
        windowAssigner.setReport(report);
        windowAssigner.setTick(tick);
        windowAssigner.setReportGrain(report_grain);

        StreamViewImpl v = new StreamViewImpl();
        TimeVarying timeVarying = windowAssigner.setView(v);

        Tester tester = new Tester();

        v.addObserver((o, arg) -> {
            Long arg1 = (Long) arg;
            Graph g = (Graph) timeVarying.eval(arg1);
            tester.test(g);
            System.err.println(arg1);
            System.err.println(g);
        });

        Graph expected = RDFUtils.getInstance().createGraph();

        Graph graph = RDFUtils.getInstance().createGraph();
        Triple triple = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S1"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O1"));
        graph.add(triple);
        expected.add(triple);

        tester.setExpected(graph);
        //RUNTIME DATA
        windowAssigner.notify(new WritableStream.Elem(1000, graph));

        graph = RDFUtils.getInstance().createGraph();
        Triple triple1 = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S2"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O2"));
        graph.add(triple1);

        expected.add(triple1);
        tester.setExpected(expected);
        windowAssigner.notify(new WritableStream.Elem(1000, graph));

        graph = RDFUtils.getInstance().createGraph();
        Triple triple2 = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S3"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O3"));

        graph.add(triple2);
        expected.add(triple2);
        tester.setExpected(expected);

        windowAssigner.notify(new WritableStream.Elem(2001, graph));
        graph = RDFUtils.getInstance().createGraph();

        Triple triple3 = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S4"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O4"));
        graph.add(triple3);

        expected.add(triple3);
        tester.setExpected(expected);

        windowAssigner.notify(new WritableStream.Elem(3000, graph));

        graph = RDFUtils.getInstance().createGraph();
        Triple triple4 = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S5"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O5"));
        graph.add(triple4);

        expected.add(triple4);
        tester.setExpected(expected);

        windowAssigner.notify(new WritableStream.Elem(5000, graph));

        graph = RDFUtils.getInstance().createGraph();
        Triple triple5 = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S6"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O6"));
        graph.add(triple5);

        expected.add(triple5);
        tester.setExpected(expected);

        windowAssigner.notify(new WritableStream.Elem(5000, graph));
        windowAssigner.notify(new WritableStream.Elem(6000, graph));

        graph = RDFUtils.getInstance().createGraph();
        Triple triple6 = RDFUtils.getInstance().createTriple(RDFUtils.getInstance().createIRI("S7"), RDFUtils.getInstance().createIRI("p"), RDFUtils.getInstance().createIRI("O7"));
        graph.add(triple6);

        expected.add(triple6);
        tester.setExpected(expected);

        windowAssigner.notify(new WritableStream.Elem(7000, graph));
        //stream.put(new WritableStream.Elem(3000, graph));


    }

    private class Tester {

        Graph expected;

        public void test(Graph g) {
            g.stream().map(Triple.class::cast).forEach(triple ->
                    assertTrue(expected.contains(triple)));
        }

        public void setExpected(Graph expected) {
            this.expected = expected;
        }

    }
}