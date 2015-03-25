package org.talend.camel.designer.codegen.argument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.camel.designer.codegen.config.ECamelTemplate;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.BundleExtJetBean;
import org.talend.designer.codegen.config.JetBean;
import org.talend.designer.codegen.config.NodesTree;
import org.talend.designer.codegen.model.CodeGeneratorInternalTemplatesFactoryProvider;

public abstract class ArgumentBuilderHolder {

    private static Map<ECamelTemplate, JetBean> templateJetBeans = new HashMap<ECamelTemplate, JetBean>();

    protected final CodeGeneratorArgumentBuilder argumentBuilder;

    protected final RouteProcess process;

    protected final NodesTree processTree;

    protected final List<INode> rootNodes;

    public ArgumentBuilderHolder(CodeGeneratorArgumentBuilder argumentBuilder) {
        this.argumentBuilder = argumentBuilder;
        process = argumentBuilder.getProcess();
        processTree = argumentBuilder.getProcessTree();
        rootNodes = processTree.getRootNodes();
    }

    public CodeGeneratorArgumentBuilder getArgumentBuilder() {
        return argumentBuilder;
    }

    /**
     * 
     * get the template jet bean, this is more similar with AbstractCodeGenerator.getTemplateJetBean.
     */
    protected JetBean getTemplateJetBean(ECamelTemplate type) {
        JetBean jetBean = templateJetBeans.get(type);
        if (jetBean == null) {
            List<BundleExtJetBean> bundleJetBeans = CodeGeneratorInternalTemplatesFactoryProvider.getInstance()
                    .getBundleExtJetBeans();
            for (BundleExtJetBean bundleBean : bundleJetBeans) {
                if (bundleBean.getName().equals(type.getTemplateName())) {
                    try {
                        jetBean = bundleBean.clone();
                        templateJetBeans.put(type, jetBean);
                        break;
                    } catch (CloneNotSupportedException e) {
                        //
                    }
                }
            }
        }
        return jetBean;
    }
}
