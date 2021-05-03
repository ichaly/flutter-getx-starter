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

    //default true: use high mode
    public boolean defaultMode = GetXConfig.defaultMode;

    //default true
    public boolean useFolder = GetXConfig.useFolder;

    //default false
    public boolean usePrefix = GetXConfig.usePrefix;

    //auto dispose GetXController
    public boolean autoDispose = GetXConfig.autoDispose;

    //Logical layer name
    public String logicName = GetXConfig.logicName;

    //view layer name
    public String viewName = GetXConfig.viewName;

    //state layer name
    public String stateName = GetXConfig.stateName;

    @Override
    public DataService getState() {
        return this;
    }

    @Override
    public void loadState(DataService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}