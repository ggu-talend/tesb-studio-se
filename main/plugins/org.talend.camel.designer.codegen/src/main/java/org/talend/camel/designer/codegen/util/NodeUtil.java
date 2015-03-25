package org.talend.camel.designer.codegen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.camel.designer.codegen.i18n.Messages;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;

public class NodeUtil {

    /**
     * DOC xtan for debug
     * <p>
     * output the nodes info to console to check the intial input info from design level.
     * </p>
     */
    public static void printForDebug(List<? extends INode> nodes) {
        // get unique name
        List<String> nameList = new ArrayList<String>(nodes.size());
        for (INode node : nodes) {
            nameList.add(node.getUniqueName());
        }

        // sort in nameList, in order to keep the intial node inder in nodes.
        Collections.sort(nameList);

        for (String string : nameList) {
            for (INode node : nodes) {
                if (string.equals(node.getUniqueName())) {
                    // output the node info
                    System.out.println(node);
                    break;
                }
            }
        }

        System.out.println(Messages.getString("NodeUtil.newLine")); //$NON-NLS-1$
    }

    /**
     * Return Type of Node to correctly sort the encapsulated code.
     * 
     * @param node the node to check
     * @return true if the node is an iterate node
     */
    public static boolean isSpecifyInputNode(INode node, String incomingName, EConnectionType connectionType) {
        // it means the first node without any income connection
        if (node == null || incomingName == null || connectionType == null) {
            return false;
        }
        List<? extends IConnection> inComingIterateConnection = node.getIncomingConnections(connectionType);
        if (inComingIterateConnection == null) {
            return false;
        }
        for (IConnection connection : inComingIterateConnection) {
            if (connection.getName().equals(incomingName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMessagingFamilyStartNode(INode node) {
        if (node == null) {
            return false;
        }
        IElementParameter family = node.getElementParameter("FAMILY");
        return node.isStart() && null != family && "Messaging".equals(family.getValue());
    }

    public static boolean isConfigComponentNode(INode subProcessStartNode) {
        String startNodeName = subProcessStartNode.getComponent().getName();
        if ("cConfig".equals(startNodeName)) {
            // Customized remove the cConfig routeId codes.
            // TESB-2825 LiXP 20110823
            return true;
        }
        return false;
    }

    public static boolean isStartNode(INode node) {
        return node.getIncomingConnections().size() < 1 && node.isStart();
    }
}
