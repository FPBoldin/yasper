package it.polimi.jasper.engine.windowing;

import com.espertech.esper.client.*;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import it.polimi.jasper.engine.streaming.items.StreamItem;
import it.polimi.jasper.engine.spe.content.ContentBean;
import it.polimi.jasper.engine.spe.esper.RuntimeManager;
import it.polimi.jasper.engine.spe.content.TimeVaryingJenaGraph;
import it.polimi.yasper.simple.windowing.TimeVarying;
import it.polimi.yasper.core.spe.content.Content;
import it.polimi.yasper.core.spe.content.viewer.View;
import it.polimi.yasper.core.spe.report.Report;
import it.polimi.yasper.core.spe.report.ReportGrain;
import it.polimi.yasper.core.spe.scope.Tick;
import it.polimi.yasper.core.spe.time.Time;
import it.polimi.yasper.core.spe.time.TimeFactory;
import it.polimi.yasper.core.spe.windowing.assigner.WindowAssigner;
import it.polimi.yasper.core.stream.StreamElement;
import it.polimi.yasper.core.utils.EncodingUtils;
import it.polimi.yasper.core.utils.EngineConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.jena.graph.Graph;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class EsperWindowAssigner implements WindowAssigner<Graph>, Observer {

    private EPAdministrator admin;
    private EPStatement statement;
    private EPRuntime runtime;
    private EngineConfiguration rsp_config;
    private Time time;
    private Report report;
    private Tick tick = Tick.TIME_DRIVEN;
    private ReportGrain reportGrain = ReportGrain.SINGLE;

    public EsperWindowAssigner(String name, EPStatementObjectModel epStatementObjectModel) throws ConfigurationException {
        this.rsp_config = EngineConfiguration.getCurrent();
        this.report = rsp_config.getReport();
        this.time = TimeFactory.getInstance();
        this.runtime = RuntimeManager.getEPRuntime();
        this.admin = RuntimeManager.getAdmin();
        this.statement = admin.create(epStatementObjectModel, name);
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public Tick getTick() {
        return tick;
    }

    @Override
    public Content getContent(long now) {
        SafeIterator<EventBean> iterator = statement.safeIterator();
        ContentBean events = new ContentBean();
        events.setLast_timestamp_changed(now);
        while (iterator.hasNext()) {
            events.add(iterator.next());
        }
        return events;
    }

    @Override
    public List<Content> getContents(long now) {
        return null;
    }

    @Override
    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public void setTick(Tick tick) {
        this.tick = tick;
    }

    @Override
    public TimeVarying<Graph> setView(View content) {
        content.observerOf(statement);
        return new TimeVaryingJenaGraph();
    }

    @Override
    public void setReportGrain(ReportGrain aw) {
        this.reportGrain = aw;
    }

    @Override
    public void notify(StreamElement arg) {
        if (arg instanceof StreamItem)
            process((StreamItem) arg);
    }

    public boolean process(StreamItem g) {

        //Event time vs ingestion time
        long now = rsp_config.isUsingEventTime() ? g.getAppTimestamp() : g.getSysTimestamp();

        if (time.getAppTime() <= now) {
            time.setAppTime(now);
        }

        String encode = EncodingUtils.encode(g.getStreamURI());
        runtime.sendEvent(g, encode);
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        process((StreamItem) arg);
    }
}
