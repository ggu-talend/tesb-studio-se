package org.talend.camel.designer.codegen.partgen;

import java.util.Arrays;
import java.util.Vector;

import org.talend.camel.designer.codegen.argument.ArgumentBuilderHolder;
import org.talend.camel.designer.codegen.argument.CodeGeneratorArgumentBuilder;
import org.talend.camel.designer.codegen.config.ECamelTemplate;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.process.INode;
import org.talend.core.model.temp.ECodePart;
import org.talend.designer.codegen.config.CloseBlocksCodeArgument;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.JetBean;
import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.designer.codegen.exception.CodeGeneratorException;
import org.talend.designer.codegen.generator.JetGeneratorUtil;

public class TemplatePartGenerator extends ArgumentBuilderHolder implements PartGenerator<ECamelTemplate> {

    public TemplatePartGenerator(CodeGeneratorArgumentBuilder argumentBuilder) {
        super(argumentBuilder);
    }

    @Override
    public CharSequence generatePart(ECamelTemplate template, Object... params) throws CodeGeneratorException {
        switch (template) {
        case HEADER_ROUTE:
        case CAMEL_HEADER:
        case CAMEL_FOOTER: {
            Object[] args = { process, VersionUtils.getVersion(), /* exportAsOSGI= */"false" };
            return generateTypedComponentCode(template, wrapToVector(args));
        }
        case FOOTER_ROUTE:
            return generateTypedComponentCode(template, wrapToVector(process, rootNodes));

        case PROCESSINFO:
        case SUBPROCESS_FOOTER_ROUTE:
        case SUBPROCESS_HEADER_ROUTE:
            return generateTypedComponentCode(template, params[0]);

        case CLOSE_BLOCKS_CODE: {
            INode node = (INode) params[0];
            CloseBlocksCodeArgument closeBlocksArgument = new CloseBlocksCodeArgument();
            closeBlocksArgument.setBlocksCodeToClose(node.getBlocksCodeToClose());
            return generateTypedComponentCode(template, closeBlocksArgument);
        }

        default:
            return generateTypedComponentCode(template, params);
        }
    }

    private CharSequence generateTypedComponentCode(ECamelTemplate template, Object... params) throws CodeGeneratorException {
        Object argument = null;
        if (params.length > 0) {
            argument = params[0];
        }
        ECodePart part = null;
        String incomingName = null;
        NodesSubTree subProcess = null;
        if (params.length > 1) {
            for (int i = 1; i < params.length; i++) {
                if (params[i] instanceof ECodePart) {
                    part = (ECodePart) params[i];
                } else if (params[i] instanceof String) {
                    incomingName = (String) params[i];
                } else if (params[i] instanceof NodesSubTree) {
                    subProcess = (NodesSubTree) params[i];
                }
            }
        }
        return generateTypedComponentCode(template, argument, part, incomingName, subProcess);
    }

    private CharSequence generateTypedComponentCode(ECamelTemplate template, Object argument) throws CodeGeneratorException {
        return generateTypedComponentCode(template, argument, null, null, null);
    }

    /**
     * Generate Code Part for a given Component.
     * 
     * @param type the internal component template
     * @param argument the bean
     * @param part part of code to generate
     * @param subProcess
     * @return the genrated code
     * @throws CodeGeneratorException if an error occurs during Code Generation
     */
    private CharSequence generateTypedComponentCode(ECamelTemplate type, Object argument, ECodePart part, String incomingName,
            NodesSubTree subProcess) throws CodeGeneratorException {
        CodeGeneratorArgument codeGenArgument = argumentBuilder.build();
        codeGenArgument.setNode(argument);
        if (subProcess != null) {
            codeGenArgument.setAllMainSubTreeConnections(subProcess.getAllMainSubTreeConnections());
            codeGenArgument.setSubTree(subProcess);
        }
        codeGenArgument.setCodePart(part);
        codeGenArgument.setIncomingName(incomingName);

        // JetBean jetBean = JetUtil.createJetBean(codeGenArgument);
        // jetBean.setTemplateRelativeUri(type.getTemplateURL());

        JetBean jetBean = getTemplateJetBean(type);
        return JetGeneratorUtil.jetGenerate(jetBean, codeGenArgument);
    }

    private static Vector<Object> wrapToVector(Object... objs) {
        return new Vector<Object>(Arrays.asList(objs));
    }

}
