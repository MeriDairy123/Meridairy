package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;
import java.util.List;


/* compiled from: RateCardSync.kt */
public final class RateCardSync {
    private List<List<String>> add = new ArrayList();
    private List<String> delete = new ArrayList();
    private long since;
    private List<List<String>> update = new ArrayList();

    public final List<List<String>> getAdd() {
        return this.add;
    }

    public final List<String> getDelete() {
        return this.delete;
    }

    public final long getSince() {
        return this.since;
    }

    public final List<List<String>> getUpdate() {
        return this.update;
    }

    public final void setAdd(List<List<String>> list) {

        this.add = list;
    }

    public final void setDelete(List<String> list) {
        this.delete = list;
    }

    public final void setSince(long j) {
        this.since = j;
    }

    public final void setUpdate(List<List<String>> list) {

        this.update = list;
    }
}
