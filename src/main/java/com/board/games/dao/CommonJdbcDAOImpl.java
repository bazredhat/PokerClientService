/**
 * This file is part of PokerClientService.
 * @copyright (c) 2015 Cuong Pham-Minh
 *
 * PokerClientService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PokerClientService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PokerClientService.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.board.games.dao;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.ini4j.Ini;

public class CommonJdbcDAOImpl {

	private Logger log = Logger.getLogger(this.getClass());
	
	private String siteUrl = "";
	private String dbPrefix = "";
	private String useIntegrations = "Y";	
    private String dbBoard = "";
    private String dbNetworkUserService = "";
    private String dbNetWorkWalletService = "";
	
	protected void initialize() {
		try {
			log.warn("Before Reading config in ini file");
			Ini ini = new Ini(new File("JDBCConfig.ini"));
			log.warn("After Reading config in ini file");
		    siteUrl = ini.get("JDBCConfig", "siteUrl");
		    log.warn("siteUrl " + siteUrl);
			dbPrefix = ini.get("JDBCConfig", "dbPrefix");
			useIntegrations = ini.get("JDBCConfig", "useIntegrations");
			
			dbNetworkUserService = ini.get("JDBCConfig", "dbNetworkUserService");
			dbNetWorkWalletService = ini.get("JDBCConfig", "dbNetWorkWalletService");
			dbBoard = ini.get("JDBCConfig", "dbBoard");
				
			
		} catch(IOException ioe) {
			log.error("Exception in selectPlayerProfile " + ioe.toString());
		} catch (Exception e) {
			log.error("Exception in selectPlayerProfile " + e.toString());
			throw e;
		}

		
	}

	public Logger getLog() {
		return log;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public String getDbPrefix() {
		return dbPrefix;
	}


	public String getUseIntegrations() {
		return useIntegrations;
	}



	public String getDbBoard() {
		return dbBoard;
	}

	public String getDbNetworkUserService() {
		return dbNetworkUserService;
	}

	public String getDbNetWorkWalletService() {
		return dbNetWorkWalletService;
	}
}
