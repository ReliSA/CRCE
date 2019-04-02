package cz.zcu.kiv.crce.crce_component_collection.api.settings;

import cz.zcu.kiv.crce.crce_component_collection.api.impl.LimitRange;

public class SettingsLimitRange {
    private LimitRange exportArtifactRange = LimitRange.MAX;
    private String exportPath = "tmp";

    public LimitRange getExportArtifactRange() {
        return exportArtifactRange;
    }

    public void setExportArtifactRange(LimitRange exportArtifactRange) {
        this.exportArtifactRange = exportArtifactRange;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }
}
