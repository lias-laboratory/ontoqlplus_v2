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
package fr.ensma.lias.mariusqlplus.workbench;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.ensma.lias.mariusqlplus.common.Environment;
import fr.ensma.lias.mariusqlplus.datatable.ResultGridPanel;

/**
 * @author Stéphane JEAN
 */
public class WBGeneratedSQLAction extends AbstractAction {

    private static final long serialVersionUID = -5674911584333940279L;

    private MariusQLWorkBench wb;

    public WBGeneratedSQLAction(String name, MariusQLWorkBench parent) {
	super(name, Environment.GENERATED_SQL_ICON);
	this.wb = parent;
    }

    public void actionPerformed(ActionEvent e) {
	ResultGridPanel rgp = wb.getRgp();
	CommandEditorPanel cep = wb.getCep();
	cep.setEditorText(cep.getEditorText() + "\n\nGenerated SQL :\n"
		+ rgp.sqlString);
    }
}
