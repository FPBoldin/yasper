package it.polimi.jasper.engine.spe.esper;

import com.espertech.esper.client.soda.*;
import it.polimi.jasper.engine.windowing.EsperWindowAssigner;
import it.polimi.yasper.core.enums.WindowType;
import it.polimi.yasper.core.spe.windowing.assigner.WindowAssigner;
import it.polimi.yasper.core.stream.Stream;
import it.polimi.yasper.core.utils.EncodingUtils;
import lombok.extern.log4j.Log4j;
import org.apache.commons.configuration.ConfigurationException;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by riccardo on 04/09/2017.
 */
@Log4j
public class EPLFactory {



    public static EPStatementObjectModel toEPL(int step, String unitStep, WindowType type, String s, View window, List<AnnotationPart> annotations) {
        EPStatementObjectModel stmt = new EPStatementObjectModel();

        stmt.setAnnotations(annotations);
        SelectClause selectClause = SelectClause.create().addWildcard();
        stmt.setSelectClause(selectClause);
        FromClause fromClause = FromClause.create();
        FilterStream stream = FilterStream.create(EncodingUtils.encode(s));
        stream.addView(window);
        fromClause.add(stream);
        stmt.setFromClause(fromClause);

        OutputLimitClause outputLimitClause;

        if (WindowType.Physical.equals(type)) {
            outputLimitClause = OutputLimitClause.create(OutputLimitSelector.SNAPSHOT, step);
        } else {
            TimePeriodExpression timePeriod = getTimePeriod(step, unitStep);
            outputLimitClause = OutputLimitClause.create(OutputLimitSelector.SNAPSHOT, timePeriod);
        }

        stmt.setOutputLimitClause(outputLimitClause);
        return stmt;
    }



    public static List<AnnotationPart> getAnnotations(String name1, int range1, int step1, String s) {
        AnnotationPart name = new AnnotationPart();
        name.setName("Name");
        name.addValue(EncodingUtils.encode(name1));

        AnnotationPart range = new AnnotationPart();
        range.setName("Tag");
        range.addValue("name", "range");
        range.addValue("value", range1 + "");

        AnnotationPart slide = new AnnotationPart();
        slide.setName("Tag");
        slide.addValue("name", "step");
        slide.addValue("value", step1 + "");

        AnnotationPart stream_uri = new AnnotationPart();
        stream_uri.setName("Tag");
        stream_uri.addValue("name", "stream");
        stream_uri.addValue("value", (EncodingUtils.encode(s)));

        return Arrays.asList(name, stream_uri, range, slide);
    }


    public static View getWindow(int range, String unitRange, WindowType type) {
        View view;
        ArrayList<Expression> parameters = new ArrayList<Expression>();
        if (WindowType.Physical.equals(type)) {
            parameters.add(Expressions.constant(range));
            view = View.create("win", "length", parameters);
        } else {
            parameters.add(getTimePeriod(range, unitRange));
            view = View.create("win", "time", parameters);
        }
        return view;
    }

    private static TimePeriodExpression getTimePeriod(Integer omega, String unit_omega) {
        String unit = unit_omega.toLowerCase();
        if ("ms".equals(unit) || "millis".equals(unit) || "milliseconds".equals(unit)) {
            return Expressions.timePeriod(null, null, null, null, omega);
        } else if ("s".equals(unit) || "seconds".equals(unit) || "sec".equals(unit)) {
            return Expressions.timePeriod(null, null, null, omega, null);
        } else if ("m".equals(unit) || "minutes".equals(unit) || "min".equals(unit)) {
            return Expressions.timePeriod(null, null, omega, null, null);
        } else if ("h".equals(unit) || "hours".equals(unit) || "hour".equals(unit)) {
            return Expressions.timePeriod(null, omega, null, null, null);
        } else if ("d".equals(unit) || "days".equals(unit)) {
            return Expressions.timePeriod(omega, null, null, null, null);
        }
        return null;
    }

    public static String toEPLSchema(Stream s) {
        CreateSchemaClause schema = new CreateSchemaClause();
        schema.setSchemaName(EncodingUtils.encode(s.getURI()));
        schema.setInherits(new HashSet<String>(Arrays.asList(new String[]{"TStream"})));
        List<SchemaColumnDesc> columns = Arrays.asList(
                new SchemaColumnDesc("sys_timestamp", "long", false),
                new SchemaColumnDesc("app_timestamp", "long", false),
                new SchemaColumnDesc("content", Object.class.getTypeName(), false));
        schema.setColumns(columns);
        StringWriter writer = new StringWriter();
        schema.toEPL(writer);
        return writer.toString();
    }

    public static WindowAssigner getWindowAssigner(String name, int step, int range, String unitStep, String unitRange, WindowType type) {
        List<AnnotationPart> annotations = EPLFactory.getAnnotations(name, range, step, name);
        View window = EPLFactory.getWindow(range, unitRange, type);
        EPStatementObjectModel epStatementObjectModel = EPLFactory.toEPL(step, unitStep, type, name, window, annotations);
        try {
            return new EsperWindowAssigner(EncodingUtils.encode(name), epStatementObjectModel);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error During Stream Registration");
        }
    }


}
