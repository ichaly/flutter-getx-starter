package cn.te0.flutter.helper;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * @author chaly
 */
@com.intellij.openapi.components.State(
    name = "DataService",
    storages = {@Storage(value = "DataService.xml")}
)
public class DataService implements PersistentStateComponent<DataService> {
    public static DataService getInstance() {
        return ServiceManager.getService(DataService.class);
    }

    public boolean isPage = false;
    public boolean useFolder = true;
    public boolean usePrefix = true;
    public boolean defaultMode = true;
    public boolean autoDispose = false;

    @Override
    public DataService getState() {
        return this;
    }

    @Override
    public void loadState(DataService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
