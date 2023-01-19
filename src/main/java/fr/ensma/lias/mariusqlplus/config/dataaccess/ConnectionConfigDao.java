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
package fr.ensma.lias.mariusqlplus.config.dataaccess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.mariusqlplus.common.Environment;
import fr.ensma.lias.mariusqlplus.config.ConnectionConfig;

/**
 * @author Valentin CASSAIR
 */
public class ConnectionConfigDao {

    public static void writeConfigs(List<ConnectionConfig> configList) {
	try {
	    FileOutputStream fichier = new FileOutputStream(
		    Environment.CONN_CONFIG_FILE_PATH);
	    ObjectOutputStream oos = new ObjectOutputStream(fichier);
	    oos.writeObject(configList);
	    oos.flush();
	    oos.close();
	} catch (java.io.IOException e) {
	    e.printStackTrace();
	}
    }

    @SuppressWarnings({ "unchecked", "resource" })
    public static List<ConnectionConfig> loadSavedConfigs() {
	List<ConnectionConfig> configList = new ArrayList<ConnectionConfig>();
	try {
	    FileInputStream fichier = new FileInputStream(
		    Environment.CONN_CONFIG_FILE_PATH);
	    ObjectInputStream ois = new ObjectInputStream(fichier);
	    configList = (List<ConnectionConfig>) ois.readObject();
	} catch (java.io.IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	return configList;
    }
}
