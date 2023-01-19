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
package fr.ensma.lias.mariusqlplus.common.util;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * @author Stéphane JEAN
 */
public class GuiUtils {

    protected Component c;

    public GuiUtils(Component c) {
        this.c = c;
    } 

    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(c, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void displayInfo(String infoMessage) {
        JOptionPane.showMessageDialog(c, infoMessage, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayWarning(String errorMessage) {
        JOptionPane.showMessageDialog(c, errorMessage, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public int requestConfirm(String confirmMessage) {
        return JOptionPane.showConfirmDialog(c, confirmMessage,
                    "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
}
