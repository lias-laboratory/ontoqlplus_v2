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

import java.io.Serializable;
import java.util.Properties;

/**
 * @author Valentin CASSAIR
 */
public class ConnectionConfig implements Comparable<Object>, Serializable {

    private static final long serialVersionUID = 1909748018671668495L;

    protected Properties props = new Properties();

    public ConnectionConfig(Properties props) {
	this.props = props;
    }

    public boolean equals(Object arg) {
	if (!(arg instanceof ConnectionConfig))
	    return false;
	
	ConnectionConfig other = (ConnectionConfig) arg;
	return props.getProperty("name").equals(other.getName())
		&& props.getProperty("server.host").equals(other.getIp())
		&& Integer.parseInt(props.getProperty("server.port")) == other
			.getPort()
		&& props.getProperty("server.sid").equals(other.getSid())
		&& props.getProperty("server.user").equals(other.getUser())
		&& props.getProperty("server.password").equals(
			other.getPassword())
		&& props.getProperty("driver.class").equals(
			other.getUrlDriver());
    }

    public String toString() {
	return getName();
    }

    public String url() {
	return "jdbc:postgresql://" + getIp() + ":" + getPort() + "/"
		+ getSid();
    }

    public int compareTo(Object arg) {
	ConnectionConfig other = (ConnectionConfig) arg;
	return getName().toLowerCase().compareTo(other.getName().toLowerCase());
    }

    public String getName() {
	return props.getProperty("name");
    }

    public String getIp() {
	return props.getProperty("server.host");
    }

    public int getPort() {
	return Integer.parseInt(props.getProperty("server.port"));
    }

    public String getSid() {
	return props.getProperty("server.sid");
    }

    public String getUser() {
	return props.getProperty("server.user");
    }

    public String getPassword() {
	return props.getProperty("server.password");
    }

    public String getDriver() {
	if (props.getProperty("driver.class").contains("hsql")) {
	    return ConnectionConfigPanel.HSQLDB;
	} else if (props.getProperty("driver.class").contains("postgres")){
	    return ConnectionConfigPanel.POSTGRESQL;
	}
	else {
	    return ConnectionConfigPanel.ORACLE;
	}
    }

    public String getUrlDriver() {
	return props.getProperty("driver.class");
    }

    public Properties getProps() {
	return props;
    }
}
