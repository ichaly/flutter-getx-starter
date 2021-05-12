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

    public boolean defaultMode = GetXConfig.defaultMode;
    public boolean useFolder = GetXConfig.useFolder;
    public boolean usePrefix = GetXConfig.usePrefix;
    public boolean autoDispose = GetXConfig.autoDispose;
    public boolean isPage = GetXConfig.isPage;

    public String logicName = GetXConfig.logicName;
    public String viewName = GetXConfig.viewName;
    public String stateName = GetXConfig.stateName;
    public String pageName = GetXConfig.pageName;

    @Override
    public DataService getState() {
        return this;
    }

    @Override
    public void loadState(DataService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}