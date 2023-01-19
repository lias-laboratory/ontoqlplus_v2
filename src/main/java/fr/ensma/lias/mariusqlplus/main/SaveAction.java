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

import fr.ensma.lias.mariusqlplus.common.AbstractSaveAction;
import fr.ensma.lias.mariusqlplus.workbench.MariusQLWorkBench;
import fr.ensma.lias.mariusqlplus.workbench.WBSaveAction;

/**
 * @author Stéphane JEAN
 */
public class SaveAction extends AbstractSaveAction {

    private static final long serialVersionUID = -2440715440151564545L;

    private Main main;

    public SaveAction(String name, Main parent) {
        super(name);
        this.main = parent;
    }

    public void actionPerformed(ActionEvent e) {
        if (main.isWorkBenchInFocus()) {
            MariusQLWorkBench swb = main.getWorkBenchInFocus();
            WBSaveAction saveAction = swb.getSaveAction();
            saveAction.actionPerformed(e);
        }
    }
}
