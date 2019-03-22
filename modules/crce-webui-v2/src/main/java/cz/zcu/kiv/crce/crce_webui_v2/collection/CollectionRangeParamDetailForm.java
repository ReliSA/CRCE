package cz.zcu.kiv.crce.crce_webui_v2.collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.zcu.kiv.crce.crce_component_collection.api.bean.CollectionDetailBean;
import cz.zcu.kiv.crce.crce_webui_v2.collection.classes.ArtifactRangeBean;
import cz.zcu.kiv.crce.crce_webui_v2.collection.classes.ParameterBean;
import cz.zcu.kiv.crce.crce_webui_v2.webui.MyUI;

import java.util.ArrayList;
import java.util.List;

public class CollectionRangeParamDetailForm extends FormLayout {
    private Label label = new Label("Collection Details");
    private TabSheet tabsheet = new TabSheet();
    private Button buttonBackParameters = new Button("Back");
    private Button buttonBackArtifactRange = new Button("Back");
    private Grid gridParameters = new Grid();
    private Grid gridArtifactRange = new Grid();

    public CollectionRangeParamDetailForm(MyUI myUI, CollectionDetailBean collectionDetailBean, FormLayout returnPage){
        VerticalLayout content = new VerticalLayout();
        label.addStyleName(ValoTheme.LABEL_BOLD);

        // display parameters
        VerticalLayout parametersContentLayout = new VerticalLayout();
        VerticalLayout parametersTabLayout = new VerticalLayout();
        HorizontalLayout buttonParametersLayout = new HorizontalLayout();

        buttonParametersLayout.addComponents(buttonBackParameters);
        buttonParametersLayout.setSpacing(true);

        buttonBackParameters.setWidth("130px");

        List<ParameterBean> listParameter = new ArrayList<>();
        for(String s : collectionDetailBean.getParameters()){
            String[] pom = s.split("=");
            listParameter.add(new ParameterBean(pom[0], pom[1]));
        }

        gridParameters.setContainerDataSource(new BeanItemContainer<>(ParameterBean.class, listParameter));
        gridParameters.setColumnOrder("name", "value");
        gridParameters.addStyleName("my-style");
        HorizontalLayout parametersFormLayout = new HorizontalLayout(parametersTabLayout);
        parametersFormLayout.setSizeFull();
        gridParameters.setSizeFull();
        parametersTabLayout.addComponents(gridParameters);
        parametersFormLayout.setExpandRatio(parametersTabLayout,1);

        parametersContentLayout.addComponents(parametersFormLayout, buttonParametersLayout);
        parametersContentLayout.setComponentAlignment(buttonParametersLayout, Alignment.BOTTOM_CENTER);
        parametersContentLayout.setSpacing(true);
        parametersContentLayout.setMargin(new MarginInfo(true, false));

        // display component with range
        VerticalLayout artifactRangeContentLayout = new VerticalLayout();
        VerticalLayout artifactRangeTabLayout = new VerticalLayout();
        HorizontalLayout buttonArtifactRangeLayout = new HorizontalLayout();

        buttonArtifactRangeLayout.addComponents(buttonBackArtifactRange);
        buttonArtifactRangeLayout.setSpacing(true);

        buttonBackArtifactRange.setWidth("130px");

        List<ArtifactRangeBean> listArtifactRange = new ArrayList<>();
        for(String s : collectionDetailBean.getRangeArtifacts()){
            String[] pom = s.split("=");
            listArtifactRange.add(new ArtifactRangeBean(pom[0], pom[1]));
        }

        gridArtifactRange.setContainerDataSource(new BeanItemContainer<>(ArtifactRangeBean.class, listArtifactRange));
        gridArtifactRange.setColumnOrder("name", "range");
        gridArtifactRange.addStyleName("my-style");
        HorizontalLayout artifactRangeFormLayout = new HorizontalLayout(artifactRangeTabLayout);
        artifactRangeFormLayout.setSizeFull();
        gridArtifactRange.setSizeFull();
        artifactRangeTabLayout.addComponents(gridArtifactRange);
        artifactRangeFormLayout.setExpandRatio(artifactRangeTabLayout,1);

        artifactRangeContentLayout.addComponents(artifactRangeFormLayout, buttonArtifactRangeLayout);
        artifactRangeContentLayout.setComponentAlignment(buttonArtifactRangeLayout, Alignment.BOTTOM_CENTER);
        artifactRangeContentLayout.setSpacing(true);
        artifactRangeContentLayout.setMargin(new MarginInfo(true, false));


        tabsheet.addTab(parametersContentLayout).setCaption("Parameters");
        tabsheet.addTab(artifactRangeContentLayout).setCaption("List component");

        buttonBackParameters.addClickListener(e ->{
            myUI.setContentExistingPage(returnPage);
        });

        buttonBackArtifactRange.addClickListener(e ->{
            myUI.setContentExistingPage(returnPage);
        });

        content.addComponents(label, tabsheet);
        content.setSpacing(true);
        content.setMargin(new MarginInfo(false, true));

        addComponent(content);
    }
}
