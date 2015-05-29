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

package com.board.games.svc.bean;


import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.board.games.dao.GenericDAO;
import com.board.games.model.Profile;


public class GameConfigQueryImpl implements GameConfigQuery {

	private Logger log = Logger.getLogger(this.getClass());
    private static ApplicationContext applicationContext;
    private static ApplicationContext userApplicationContext;
    private static ApplicationContext accountApplicationContext;
	
    public static ApplicationContext getAccountApplicationContext() {
		return accountApplicationContext;
	}


	public static void setAccountApplicationContext(
			ApplicationContext accountApplicationContext) {
		GameConfigQueryImpl.accountApplicationContext = accountApplicationContext;
	}


	public static ApplicationContext getuserApplicationContext() {
		return userApplicationContext;
	}


	public static void setuserApplicationContext(
			ApplicationContext userApplicationContext) {
		GameConfigQueryImpl.userApplicationContext = userApplicationContext;
	}


	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}


	public static void setApplicationContext(ApplicationContext applicationContext) {
		GameConfigQueryImpl.applicationContext = applicationContext;
	}

	private String boardConfigFile = "";
    private String daoConfig = "ipbDAO";
    private String userConfig = "userDAO";
    private String accountConfig = "accountDAO";

	public void initialize() throws Exception { 
		log.warn("**** Inside GameConfigQueryImpl Initialized NEW *** ");
		try {
			Ini ini = new Ini(new File("PokerConfig.ini"));
			String boardConfigFile = ini.get("PokerConfig", "boardConfigFile");
			log.warn("**** GameConfigQueryImpl Initialized with config file as : " + boardConfigFile + "****");

			if (boardConfigFile == null || boardConfigFile.equals("")) {
				 log.warn("boardConfigFile is null or empty");
				 throw(new Exception("BoardConfigFile cannot be null or empty"));
			}

			daoConfig = ini.get("PokerConfig", "daoConfig");
			log.warn("**** GameConfigQueryImpl Initialized with daoConfig file as : " + daoConfig + "****");

			if (daoConfig == null || daoConfig.equals("")) {
				 log.warn("daoConfig is null or empty");
				 throw(new Exception("daoConfig cannot be null or empty"));
			}
			
			
			applicationContext = new FileSystemXmlApplicationContext(boardConfigFile);
			if (applicationContext == null) {
				throw(new Exception("Application context  cannot be null"));
			}
			

		} catch (IOException ioe) {
			log.error("Exception in init " + ioe.toString());
		} catch (Exception e) {
			log.error("Exception in init " + e.toString());
		}	
	}


	
	public Profile getProfile(int userId, String externalId, int accountId)  {
		log.warn("getProfile for externalId a #" + externalId);
		if (getApplicationContext() != null) {		
	       GenericDAO genericDAO = (GenericDAO) getApplicationContext().getBean(daoConfig);
	       try  {
		       Profile profile = genericDAO.selectProfile(userId, externalId, accountId);
		       if (profile != null) {
			       log.warn(" avatar location " + profile.getExternalAvatarUrl() );
			       log.warn(" user level is #" + profile.getLevel() );
			       log.warn(" user name  is " + profile.getName() );
			       log.warn(" screen name  is " + profile.getScreenName());
			       return profile;
		       }
			} catch (Exception e) {
				log.error("Exception in getProfile " + e.toString());
			}	
		       
		} else {
			log.warn("getApplicationContext() is null");
		}		return null;
	}



}
