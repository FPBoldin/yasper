package it.polimi.yasper.core.utils;

import it.polimi.yasper.core.enums.EntailmentType;
import it.polimi.yasper.core.enums.Maintenance;
import lombok.extern.log4j.Log4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.URL;

import static it.polimi.yasper.core.utils.ConfigurationUtils.*;

/**
 * Created by riccardo on 10/07/2017.
 */
@Log4j
public class QueryConfiguration extends PropertiesConfiguration {


    public QueryConfiguration(String fileName) throws ConfigurationException {
        super(fileName);
    }


    public boolean hasTboxLocation() {
        return this.containsKey(TBOX_LOCATION);

    }

    public String getTboxLocation() {
        return this.getString(TBOX_LOCATION, "");

    }

    public Maintenance getSdsMaintainance() {
        return Maintenance.valueOf(this.getString(SDS_MAINTAINANCE));

    }

    public Boolean getReasoningActive() {
        return this.getBoolean(REASONING_ACTIVE);

    }

    public EntailmentType getReasoningEntailment() {
        return EntailmentType.valueOf(this.getString(REASONING_ENTAILMENT));

    }

    public String getReasoningRulePath() {
        return this.getString(REASONING_RULE_PATH);

    }

    public String getQueryClass() {
        return this.getString(QUERY_CLASS);
    }

    public boolean isRecursionEnables() {
        return this.getBoolean(QUERY_RECURSION);
    }

    public static QueryConfiguration getDefault() throws ConfigurationException {
        URL resource = QueryConfiguration.class.getResource("/default.properties");
        return new QueryConfiguration(resource.getPath());
    }
}
