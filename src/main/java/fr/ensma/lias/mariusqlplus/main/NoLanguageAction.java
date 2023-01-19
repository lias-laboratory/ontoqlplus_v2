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
package fr.ensma.lias.mariusqlplus.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.ensma.lias.mariusql.MariusQLConstants;
import fr.ensma.lias.mariusql.jdbc.MariusQLSession;

/**
 * @author Stéphane JEAN
 * @author Valentin CASSAIR
 */
public class NoLanguageAction extends AbstractAction {

    private static final long serialVersionUID = 6527350837128837429L;

    protected String language_code = MariusQLConstants.NO_LANGUAGE;
    
    protected MariusQLSession session;

    public NoLanguageAction(MariusQLSession s) {
	super("None");
	language_code = MariusQLConstants.NO_LANGUAGE;
	session = s;
    }

    public String getLanguageCode() {
	return language_code;
    }

    public void actionPerformed(ActionEvent event) {
	
	session.setReferenceLanguage(MariusQLConstants.NO_LANGUAGE);
    }

    /**
     * @return Returns the session.
     */
    public MariusQLSession getSession() {
	return session;
    }

    /**
     * @param session
     *            The session to set.
     */
    public void setSession(MariusQLSession session) {
	this.session = session;
    }
}