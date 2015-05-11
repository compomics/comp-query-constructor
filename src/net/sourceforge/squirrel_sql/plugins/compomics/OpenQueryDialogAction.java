package net.sourceforge.squirrel_sql.plugins.compomics;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.IResources;

import java.awt.event.ActionEvent;
import java.util.Arrays;

public class OpenQueryDialogAction extends SquirrelAction implements ISessionAction {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "PUBLICATION";

    /**
     * The current session.
     */
    private ISession session;

    public OpenQueryDialogAction(IApplication app, IResources resources)
            throws IllegalArgumentException {
        super(app, resources);
    }

    public void actionPerformed(ActionEvent evt) {
        ISQLEntryPanel sqlEntryPanel = null;

        if (session.getActiveSessionWindow() instanceof SessionInternalFrame) {
            sqlEntryPanel = ((SessionInternalFrame) session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
        } else if (session.getActiveSessionWindow() instanceof SQLInternalFrame) {
            sqlEntryPanel = ((SQLInternalFrame) session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
        }

        // get column names
        ExtendedColumnInfo[] extendedColumnInfos = session.getSchemaInfo().getExtendedColumnInfos(TABLE_NAME);

        String[] columnNames = new String[extendedColumnInfos.length];
        for (int i = 0; i < extendedColumnInfos.length; i++) {
            columnNames[i] = extendedColumnInfos[i].getColumnName();
        }

        Arrays.sort(columnNames);
        if (sqlEntryPanel != null) {
            new OpenQueryDialogCommand(getParentFrame(evt), session, sqlEntryPanel, columnNames).execute();
        }
    }

    public void setSession(ISession session) {
        this.session = session;
    }

}
