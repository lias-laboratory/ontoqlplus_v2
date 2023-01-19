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

import fr.ensma.lias.mariusql.jdbc.MariusQLSession;
import fr.ensma.lias.mariusql.util.MariusQLHelper;
import fr.ensma.lias.mariusqlplus.common.Environment;

/**
 * @author Stéphane JEAN
 * @author Valentin CASSAIR
 */
public class EnglishLanguageAction extends AbstractAction {

    private static final long serialVersionUID = -1402502472430169201L;

    protected String language_code = MariusQLHelper.ENGLISH;

    protected MariusQLSession session;

    public EnglishLanguageAction(MariusQLSession s) {

	super("English", Environment.ENGLISH_ICON);
	language_code = MariusQLHelper.ENGLISH;
	session = s;

    }

    public String getLanguageCode() {
	return language_code;
    }

    public void actionPerformed(ActionEvent event) {
	session.setReferenceLanguage(MariusQLHelper.ENGLISH);
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