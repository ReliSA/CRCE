package cz.zcu.kiv.crce.crce_webui_v2.collection.classes;

/**
 * Bean represents collection parameters (name / value).
 * <p/>
 * Date: 02.05.19
 *
 * @author Roman Pesek
 */
public class ParameterBean {
    private String name;
    private String value;

    public ParameterBean(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
