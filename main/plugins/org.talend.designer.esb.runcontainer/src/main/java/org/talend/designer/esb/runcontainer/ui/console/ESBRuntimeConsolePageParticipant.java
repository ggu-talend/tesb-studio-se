// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.esb.runcontainer.ui.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.server.RuntimeStatusChangeListener;
import org.talend.designer.esb.runcontainer.ui.actions.HaltRuntimeAction;
import org.talend.designer.esb.runcontainer.ui.actions.OpenRuntimeInfoAction;
import org.talend.designer.esb.runcontainer.ui.actions.StartRuntimeAction;

public class ESBRuntimeConsolePageParticipant implements IConsolePageParticipant {

    private StartRuntimeAction startRuntimeAction;

    private HaltRuntimeAction haltRuntimeAction;

    private OpenRuntimeInfoAction openRuntimeInfoAction;

    private RuntimeStatusChangeListener serverListener;

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public void init(IPageBookViewPage page, IConsole console) {

        startRuntimeAction = new StartRuntimeAction();
        haltRuntimeAction = new HaltRuntimeAction();
        openRuntimeInfoAction = new OpenRuntimeInfoAction();
        serverListener = new RuntimeStatusChangeListener() {

            @Override
            public void stopRunning() {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        startRuntimeAction.setEnabled(true);
                        haltRuntimeAction.setEnabled(false);
                    }
                });
            }

            @Override
            public void startRunning() {
                // TODO Auto-generated method stub
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        startRuntimeAction.setEnabled(false);
                        haltRuntimeAction.setEnabled(true);
                    }
                });
            }

            @Override
            public void featureUninstalled(int id) {

            }

            @Override
            public void featureInstalled(int id) {

            }
        };
        RuntimeServerController.getInstance().addStatusChangeListener(serverListener);

        IActionBars actionBars = page.getSite().getActionBars();
        configureToolBar(actionBars.getToolBarManager());
    }

    private void configureToolBar(IToolBarManager mgr) {
        mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, startRuntimeAction);
        mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, haltRuntimeAction);
        mgr.appendToGroup(IConsoleConstants.OUTPUT_GROUP, openRuntimeInfoAction);
    }

    @Override
    public void dispose() {
        // startRuntimeAction = null;
        // rebootRuntimeAction = null;
        // haltRuntimeAction = null;
        // openRuntimeInfoAction = null;
    }

    @Override
    public void activated() {
        // TODO Auto-generated method stub
    }

    @Override
    public void deactivated() {
        // TODO Auto-generated method stub
    }

}
