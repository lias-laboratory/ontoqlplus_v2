/*********************************************************************************
 * This file is part of MariusQLPlus Project.
 * Copyright (C) 2014  LIAS - ENSMA
 *   Teleport 2 - 1 avenue Clement Ader
 *   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
 * 
 * MariusQLPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MariusQLPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MariusQLPlus.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package fr.ensma.lias.mariusqlplus.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.ensma.lias.mariusql.exception.MariusQLException;
import fr.ensma.lias.mariusql.jdbc.MariusQLSession;
import fr.ensma.lias.mariusql.jdbc.impl.MariusQLSessionImpl;
import fr.ensma.lias.mariusqlplus.common.Environment;
import fr.ensma.lias.mariusqlplus.common.WaitAndBlockPane;
import fr.ensma.lias.mariusqlplus.common.util.GuiUtils;
import fr.ensma.lias.mariusqlplus.main.Main;

/**
 * @author Mickael BARON
 * @author Valentin CASSAIR
 */
public class ConnectionConfigDialog extends JDialog {

    private static final long serialVersionUID = 6551984935007159704L;

    private Main main;

    private MariusQLSession session;

    public GuiUtils gUtils = new GuiUtils(this);

    private ConnectionConfigPanel ccp = new ConnectionConfigPanel(this);


    public ConnectionConfigDialog(Main main) {
	super(main, "Session configurations", true);
	this.main = main;
	setResizable(false);
	getContentPane().add(ccp);
	setGlassPane(new WaitAndBlockPane());
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    SwingUtilities.updateComponentTreeUI(this);
	    // force chaque composant de la fenêtre à appeler sa méthode
	    // updateUI
	} catch (InstantiationException e) {
	} catch (ClassNotFoundException e) {
	} catch (UnsupportedLookAndFeelException e) {
	} catch (IllegalAccessException e) {
	}
	pack();
	setLocationRelativeTo(main);
    }

    public void setSession(Properties props) throws SQLException,
	    MariusQLException {
	
	props.setProperty("server.filename", Environment.serverFileName);
	try {
	    session = new MariusQLSessionImpl(props);
	    session.setAutoCommit(true);
	} catch (Exception e) {
	    gUtils.displayWarning("Warning: Unable to connect to database : \n "
		    + e.getMessage());
	}
	main.updateAppMenuBar();

    }

    public boolean sessionEstablished() {
	return session != null;
    }

    public MariusQLSession getSession() {
	return session;
    }

    public void closeSession() {
	if (session != null) {
	    try {
		session.close();
		System.out.println("Session successfully closed..."); // DEBUG
		session = null;
	    } catch (Exception sqle) {
		main.setStatusBarText(sqle.getMessage());
		gUtils.displayWarning("Warning: Unable to close previously open "
			+ "session:\n" + sqle.getMessage());
	    }
	}
    }

}
