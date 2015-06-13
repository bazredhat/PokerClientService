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

package com.board.games.dao.nodebb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.board.games.dao.CommonJdbcDAOImpl;
import com.board.games.dao.GenericDAO;
import com.board.games.model.PlayerProfile;
import com.board.games.model.Profile;
import com.board.games.service.wallet.WalletAdapter;
import com.cubeia.backoffice.accounting.api.Money;
import com.cubeia.backoffice.wallet.api.dto.AccountBalanceResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;



public class NodeBBJdbcDAOImpl extends CommonJdbcDAOImpl implements GenericDAO {

	private Logger log = Logger.getLogger(this.getClass());
	
	private DataSource dataSource;
  
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	// Profile model used for client
	public Profile selectProfile(int id, String externalId, int accountId) throws Exception {
		initialize();
		log.warn("Inside selectProfile player id " + id + " externalId " + externalId);
		PlayerProfile playerProfile = selectPlayerProfile(id,externalId,accountId,true);
		Profile profile = new Profile();
		if (playerProfile != null) {
			profile.setExternalAvatarUrl(playerProfile.getAvatar_location());
			profile.setLevel(playerProfile.getLevel());
			profile.setName(playerProfile.getName());
			profile.setScreenName(playerProfile.getName());
			profile.setUserName(playerProfile.getName());
			profile.setExternalUsername(playerProfile.getName());
		} else {

		}
		return profile;
	}
	
	
	private PlayerProfile selectPlayerProfile(int id, String externalId, int accountId, boolean useExternalId) throws Exception{
		WalletAdapter walletAdapter = null;

		if (getUseIntegrations().equals("Y")) {
			walletAdapter = new WalletAdapter();
		}

	String query = "";

		query = "select " +
	    " n1.externalId, " + 
		" n1.id,    " +
		" n3.id   " +
		" from " + getDbNetworkUserService() + "." +  "User n1  " +
		" inner join " + getDbNetworkUserService() + "." +  "UserAttribute n2 on n1.id=n2.user_id   " +
		" inner join " + getDbNetWorkWalletService() + "." +  "Account n3 on n1.id=n3.userId   " +
		" where  " +
		(useExternalId ? " n1.id= ? " : " n1.externalId= ? ") +
		" and  " +
		" n3.name = n2.value ";
		
		log.warn("**** Query " + query);
		log.warn("User id to query " + (useExternalId ? externalId : id));
		/**
		 * Define the connection, preparedStatement and resultSet parameters
		 */
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			/**
			 * Open the connection
			 */
			connection = dataSource.getConnection();
			/**
			 * Prepare the statement
			 */
			preparedStatement = connection.prepareStatement(query);
			/**
			 * Bind the parameters to the PreparedStatement
			 */
			// use ipb user id instead only the other for account balance
			log.warn("external id to look : " +  Integer.parseInt(externalId));
			preparedStatement.setInt(1, (useExternalId? Integer.parseInt(externalId) :   id));
			/**
			 * Execute the statement
			 */
			resultSet = preparedStatement.executeQuery();
			PlayerProfile playerProfile = null;
			
			/**
			 * Extract data from the result set
			 */
			if(resultSet.next())
			{
				playerProfile = new PlayerProfile();
				String avatar_location = "";

			//	playerProfile.setId(resultSet.getInt("t2.uid"));
	//			String imageUrl = resultSet.getString("t2.avatar");
		//		avatar_location = getSiteUrl() + "/" + imageUrl;
					
			
				
	//			String name = resultSet.getString("t2.username");
		//		int groupId = resultSet.getInt("t2.usergroup");
			//	playerProfile.setGroupId(groupId);
		//		log.warn("name " + name);
			//	playerProfile.setName(name);

				int externalRealId = resultSet.getInt("n1.externalId");
	    		 // To connect to mongodb server
   	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
   	         // Now connect to your databases
   	         DB db = mongoClient.getDB( "nodebb" );
   //		 log.debug("Connect to database successfully");
			log.debug("Execute query: authenticate");
			DBCursor cursor = null;
			try {
			
   		 DBCollection collection = db.getCollection("objects");
		 log.warn("Collection find " + "user:"+String.valueOf(externalRealId));
   		 
   		 cursor = collection.find(new BasicDBObject("_key", "user:"+String.valueOf(externalRealId)),
   							   new BasicDBObject("_id", 0));
   	       
   		
			 avatar_location = (String) cursor.next().get("picture");
			 //Picture /uploads/profile/1-profileimg.png
			 log.warn("url before " + avatar_location);
			 if (avatar_location.indexOf("uploads")!= -1) {
				 log.warn("avatar is an upload avatar");
				 avatar_location =  getSiteUrl() + avatar_location; 
			 } 
				log.warn("url after " + avatar_location);	
				playerProfile.setAvatar_location(avatar_location);
			}
			finally {
				if (cursor != null) {
					cursor.close();
				}
			}
				log.warn("calling wallet account balance");
				int walletAccountId = resultSet.getInt("n3.id");
		    	AccountBalanceResult accountBalance = walletAdapter.getAccountBalance(new Long(String.valueOf(walletAccountId)));
		    	if (accountBalance != null) {
			    	Money playerMoney = (Money)accountBalance.getBalance();
			    	log.warn(walletAccountId + " has " + playerMoney.getAmount());
			    	playerProfile.setBalance(playerMoney.getAmount());
		    	}
		    	
		    	int userAccountId = resultSet.getInt("n1.id");
		    	log.warn("Getting user level for " + userAccountId);
		    	int level = walletAdapter.getUserLevel(new Long(String.valueOf(userAccountId)));
		    	log.warn("Level found : " + level);
		    	playerProfile.setLevel(level);
		    	log.warn("Level retrieved as # : " + playerProfile.getLevel());
				return playerProfile;
			}
			 else {
				log.debug("Found no user");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("SQLException : " + e.toString());
		} catch (Exception e) {
			log.error("Exception in selectPlayerProfile " + e.toString());
			throw e;
		}
		finally {
			try {
				/**
				 * Close the resultSet
				 */
				if (resultSet != null) {
					resultSet.close();
				}
				/**
				 * Close the preparedStatement
				 */
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				/**
				 * Close the connection
				 */
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				/**
				 * Handle any exception
				 */
				e.printStackTrace();
			}
		}
		return null;
	}



}
