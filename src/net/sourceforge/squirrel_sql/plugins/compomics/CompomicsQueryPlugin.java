package net.sourceforge.squirrel_sql.plugins.compomics;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.*;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;

/**
 * The Compomics query plugin.
 */
public class CompomicsQueryPlugin extends DefaultSessionPlugin {

    private PluginResources _resources;

    private interface IMenuResourceKeys {
        String COMPOMICS = "compomics";
    }

    /**
     * Listener to the SQL panel.
     */
    private ISQLPanelListener panelListener = new SQLPanelListener();

    /**
     * Return the internal name of this plugin.
     *
     * @return the internal name of this plugin.
     */
    public String getInternalName() {
        return "compomics";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return the descriptive name of this plugin.
     */
    public String getDescriptiveName() {
        return "Compomics Query Plugin";
    }

    /**
     * Returns the current version of this plugin.
     *
     * @return the current version of this plugin.
     */
    public String getVersion() {
        return "0.01";
    }

    /**
     * Returns the authors name.
     *
     * @return the authors name.
     */
    public String getAuthor() {
        return "Niels Hulstaert";
    }

    /**
     * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
     * <TT>getPluginAppSettingsFolder</TT> directory.
     *
     * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
     */
    public String getChangeLogFileName() {
        return "changes.txt";
    }

    /**
     * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
     * <TT>getPluginAppSettingsFolder</TT> directory.
     *
     * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
     */
    public String getHelpFileName() {
        return "readme.txt";
    }

    /**
     * Returns the name of the Licence file for the plugin. This should be a text or HTML file residing in the
     * <TT>getPluginAppSettingsFolder</TT> directory.
     *
     * @return the Licence file name or <TT>null</TT> if plugin doesn't have a licence file.
     */
    public String getLicenceFileName() {
        return "licence.txt";
    }

    /**
     * @return Comma separated list of contributors.
     */
    public String getContributors() {
        return "";
    }

    /**
     * Create preferences panel for the Global Preferences dialog.
     *
     * @return Preferences panel.
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        return new IGlobalPreferencesPanel[0];
    }

    /**
     * Initialize this plugin.
     */
    public synchronized void initialize() throws PluginException {
        super.initialize();

        IApplication app = getApplication();

        _resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.compomics.compomics", this);

        ActionCollection coll = app.getActionCollection();
        coll.add(new OpenQueryDialogAction(app, _resources));

        createMenu();
    }

//    public PluginSessionCallback sessionStarted(final ISession session) {
//
//        GUIUtils.processOnSwingEventThread(new Runnable() {
//            public void run() {
//                addOpenQueryDialogAction(session);
//            }
//        });
//
////        PluginSessionCallback ret = new PluginSessionCallback() {
////            public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess) {
////                ActionCollection coll = getApplication().getActionCollection();
////                sqlInternalFrame.addSeparatorToToolbar();
////                sqlInternalFrame.addToToolbar(coll.get(OpenQueryDialogAction.class));
////                sqlInternalFrame.addToToolsPopUp("openquerydialog", coll.get(OpenQueryDialogAction.class));
////
////                ISQLPanelAPI sqlPaneAPI = sqlInternalFrame.getSQLPanelAPI();
//////				CompleteBookmarkAction cba = new CompleteBookmarkAction(sess.getApplication(), _resources, sqlPaneAPI.getSQLEntryPanel(), SQLBookmarkPlugin.this);
//////				JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
//////				_resources.configureMenuItem(cba, item);
//////				JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
//////				comp.registerKeyboardAction(cba, _resources.getKeyStroke(cba), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//////				sqlInternalFrame.addToToolsPopUp("bookmarkselect", cba);
////            }
////
////            public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess) {
////            }
////        };
////        return ret;
//
//        return new PluginSessionCallbackAdaptor(this);
//    }

    /**
     * Called when a session started.
     *
     * @param session The session that is starting.
     * @return <TT>true</TT> if plugin is applicable to passed session else <TT>false</TT>.
     */
    public PluginSessionCallback sessionStarted(final ISession session) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                addOpenQueryDialogAction(session);
            }
        });

        session.getSessionInternalFrame().getSQLPanelAPI().addSQLPanelListener(panelListener);
        setupSQLEntryArea(session);

        return new PluginSessionCallbackAdaptor(this);
    }

    /**
     * Get and return a string from the plugin resources.
     *
     * @param name name of the resource string to return.
     * @return resource string.
     */
    protected String getResourceString(String name) {
        return _resources.getString(name);
    }

    /**
     * Add compomics menu.
     */
    private void createMenu() {
        final IApplication app = getApplication();
        final ActionCollection coll = app.getActionCollection();

        final JMenu menu = _resources.createMenu(IMenuResourceKeys.COMPOMICS);
        _resources.addToMenu(coll.get(OpenQueryDialogAction.class), menu);

        app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
    }

    /**
     * Add compomics menu to sql entry area.
     *
     * @param session the active session
     */
    private void setupSQLEntryArea(ISession session) {
        final ISQLPanelAPI api = session.getSessionInternalFrame().getSQLPanelAPI();
        final ActionCollection coll = getApplication().getActionCollection();
        api.addToSQLEntryAreaMenu(coll.get(OpenQueryDialogAction.class));
    }

    private void addOpenQueryDialogAction(ISession session) {

        ActionCollection coll = getApplication().getActionCollection();
        session.addSeparatorToToolbar();
        session.addToToolbar(coll.get(OpenQueryDialogAction.class));
        session.getSessionInternalFrame().addToToolsPopUp("openquerydialog", coll.get(OpenQueryDialogAction.class));

        ISQLPanelAPI sqlPaneAPI = session.getSessionInternalFrame().getSQLPanelAPI();
//		CompleteBookmarkAction cba =
//				new CompleteBookmarkAction(session.getApplication(),
//						_resources,
//						sqlPaneAPI.getSQLEntryPanel(),
//						CompomicsQueryPlugin.this);
//		JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
//		_resources.configureMenuItem(cba, item);
//		JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
//		comp.registerKeyboardAction(cba,
//				_resources.getKeyStroke(cba),
//				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//		session.getSessionInternalFrame().addToToolsPopUp("bookmarkselect", cba);
    }

    private class SQLPanelListener extends SQLPanelAdapter {
        public void sqlEntryAreaReplaced(SQLPanelEvent evt) {
            setupSQLEntryArea(evt.getSession());
        }
    }

}
