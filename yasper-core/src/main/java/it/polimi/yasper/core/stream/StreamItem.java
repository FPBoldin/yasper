package it.polimi.yasper.core.stream;

import it.polimi.rdf.RDFLine;
import it.polimi.yasper.core.query.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

public abstract class StreamItem<T> extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    protected final String appTimestamp = "app_timestamp";
    protected final String sysTimestamp = "sys_timestamp";
    protected final String content = "content";

    @Setter
    @Getter
    private String stream_uri;

    @Getter
    protected Type type;
    @Getter
    protected StreamSchema schema;


    public StreamItem(long appTimestamp1, T content1, String stream_uri) {
        this.put(appTimestamp, appTimestamp1);
        this.put(content, content1);
        this.put(sysTimestamp, System.currentTimeMillis());
        Class<T> aClass = (Class<T>) content1.getClass();
        this.type = aClass;
        this.schema = StreamSchema.Factory.wrap(aClass);
        this.stream_uri = stream_uri;
    }

    public long getAppTimestamp() {
        return this.containsKey(appTimestamp) && this.get(appTimestamp) != null ? (long) this.get(appTimestamp) : null;
    }

    public long getSysTimestamp() {
        return this.containsKey(sysTimestamp) && this.get(sysTimestamp) != null ? (long) this.get(sysTimestamp) : null;
    }

    public Type getType() {
        return type;
    }

    public T getTypedContent() {
        return this.containsKey(content) ? (T) this.get(content) : null;
    }


    public abstract Updatable addTo(Updatable abox);

    public abstract Updatable removeFrom(Updatable abox);

    public abstract Set<RDFLine> serialize();

    public abstract String getStreamURI();
}
