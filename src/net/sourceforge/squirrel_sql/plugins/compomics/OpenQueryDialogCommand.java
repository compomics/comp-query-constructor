/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.squirrel_sql.plugins.compomics;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;

/**
 * Open the Compomics query parameter dialog.
 *
 * @author Niels Hulstaert
 */
public class OpenQueryDialogCommand implements ICommand {

    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(net.sourceforge.squirrel_sql.plugins.compomics.OpenQueryDialogCommand.class);


    private static ILogger logger =
            LoggerController.createLogger(net.sourceforge.squirrel_sql.plugins.compomics.OpenQueryDialogCommand.class);

    /**
     * Parent frame.
     */
    private final Frame frame;
    /**
     * The current session.
     */
    private ISession session;
    private ISQLEntryPanel sqlEntryPanel;
    /**
     * The query input from the dialog.
     */
    private QueryInput queryInput;
    private String[] columnNames;

    /**
     * Constructor.
     *
     * @param frame         Parent Frame.
     * @param session       the session
     * @param sqlEntryPanel
     * @param columnNames
     * @throws IllegalArgumentException Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>IPlugin</TT> passed.
     */
    public OpenQueryDialogCommand(Frame frame, ISession session, ISQLEntryPanel sqlEntryPanel, String[] columnNames)
            throws IllegalArgumentException {
        super();
        this.frame = frame;
        this.session = session;
        this.sqlEntryPanel = sqlEntryPanel;
        this.columnNames = columnNames;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setQueryInput(QueryInput queryInput) {
        this.queryInput = queryInput;
    }

    /**
     * Open the query dialog and show the constructed query on the sql entry panel.
     */
    public void execute() {
        QueryConstructorDialog queryConstructorDialog = new QueryConstructorDialog(frame, this);
        queryConstructorDialog.pack();
        queryConstructorDialog.setLocationRelativeTo(frame);
        queryConstructorDialog.setVisible(true);

        if (queryInput != null) {
            // construct the query with the given input
            QueryConstructor queryConstructor = new QueryConstructor(queryInput);
            String query = queryConstructor.construct();

            if (session != null) {
                int caretPosition = sqlEntryPanel.getCaretPosition();
                sqlEntryPanel.replaceSelection(query);
                sqlEntryPanel.setCaretPosition(caretPosition + query.length());
            }
        }
    }

}
