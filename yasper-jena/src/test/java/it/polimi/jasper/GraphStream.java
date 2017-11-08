package it.polimi.jasper;

import it.polimi.jasper.engine.stream.GraphStreamItem;
import it.polimi.jasper.engine.stream.RDFStream;
import it.polimi.yasper.core.engine.RSPEngine;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.jena.rdf.model.*;

import java.util.Random;

/**
 * Created by Riccardo on 13/08/16.
 */
@Log4j
@Setter
public class GraphStream extends RDFStream implements Runnable {

    protected int grow_rate;
    private RSPEngine e;
    private String registered_stream_uri;

    public GraphStream(String name, String stream_uri, int grow_rate) {
        super(name, stream_uri);
        this.grow_rate = grow_rate;
    }

    public void setRSPEngine(RSPEngine e) {
        this.e = e;
    }

    public RSPEngine getRSPEngine() {
        return e;
    }

    public void run() {
        int i = 1;
        int j = 1;
        while (true) {
            Model m = ModelFactory.createDefaultModel();
            Random r = new Random();

            String uri = "http://www.streamreasoning/it.polimi.jasper.test/artist#";
            Resource person = ResourceFactory.createResource(registered_stream_uri + "/artist" + i);
            Resource type = ResourceFactory.createResource(uri + name);
            Property hasAge = ResourceFactory.createProperty(uri + "hasAge");
            Property hasTimestamp = ResourceFactory.createProperty(uri + "generatedAt");
            Literal age = m.createTypedLiteral(r.nextInt(99));
            Literal ts = m.createTypedLiteral(new Integer(i * 1000));

            //m.add(m.createStatement(person, RDF.type, type));
            // m.add(m.createStatement(person, hasAge, age));
            m.add(m.createStatement(person, hasTimestamp, ts));

            GraphStreamItem t = new GraphStreamItem(i * 1000, m.getGraph(), registered_stream_uri);
            System.out.println("[" + System.currentTimeMillis() + "] Sending [" + t + "] on " + registered_stream_uri + " at " + i * 1000);

            if (e != null)
                this.e.process(t);
            try {
                log.info("Sleep");
                Thread.sleep(grow_rate * 998);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i += grow_rate;
            j++;
        }
    }

}
