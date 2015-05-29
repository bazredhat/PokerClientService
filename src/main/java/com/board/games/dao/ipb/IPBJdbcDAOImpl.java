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

package com.board.games.dao.ipb;



import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.ini4j.Ini;

import com.board.games.dao.CommonJdbcDAOImpl;
import com.board.games.dao.GenericDAO;
import com.board.games.model.PlayerProfile;
import com.board.games.model.Profile;
import com.board.games.service.wallet.WalletAdapter;
import com.cubeia.backoffice.accounting.api.Money;
import com.cubeia.backoffice.wallet.api.dto.AccountBalanceResult;



public class IPBJdbcDAOImpl extends CommonJdbcDAOImpl implements GenericDAO {

	private Logger log = Logger.getLogger(this.getClass());
	
	private DataSource dataSource;
    private  boolean oldIPBVersion = false;
    private  boolean newIPB4Version = false;
	private String ipbVersion = "";

    
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
			// should handle from social network here

		}
		return profile;
	}
	
	
	private PlayerProfile selectPlayerProfile(int id, String externalId, int accountId, boolean useExternalId) throws Exception{
		WalletAdapter walletAdapter = null;
		
		log.warn("Inside selectPlayerProfile for id #" + String.valueOf(id) + (useExternalId? "USEEXTERNALID" : "NOTUSINGEXTERNALID"));
		try {
			log.warn("Before Reading config in ini file");
			Ini ini = new Ini(new File("JDBCConfig.ini"));
			log.warn("After Reading config in ini file");
			ipbVersion = ini.get("JDBCConfig", "ipbVersion");
		    log.warn("ipbVersion " + ipbVersion);
			if (!ipbVersion.equals("") && "IPS4".equals(ipbVersion.toUpperCase())) {
				newIPB4Version = true;
				log.warn("Detecting  IPS4 versionx");
			}
			
		} catch(IOException ioe) {
			log.error("Exception in selectPlayerProfile " + ioe.toString());
		} catch (Exception e) {
			log.error("Exception in selectPlayerProfile " + e.toString());
			throw e;
		}


		if (getUseIntegrations().equals("Y")) {
			walletAdapter = new WalletAdapter();
		}


		if (!ipbVersion.equals("") && "IPB3.1".equals(ipbVersion.toUpperCase())) {
			oldIPBVersion = true;
			log.warn("Detecting old IPB version 3.1.x");
		} 

		String query = "";
		if (oldIPBVersion) {
			query = "select pp_member_id,  avatar_location, avatar_type, " + 
			"name, member_group_id, posts from " + getDbPrefix() + "profile_portal a " + 
			" join " + getDbPrefix() + "members as b on a.pp_member_id=b.member_id " +
			" and b.member_id = ?";
		}
		else { 	
			if (!newIPB4Version) {
				
				query = "select " +
				" t1.pp_photo_type,  " +
				" t1.pp_gravatar,  " +
				" t1.pp_main_photo,  " +
				" t1.pp_thumb_photo,  " +
				" t1.pp_member_id,  " +
				" t1.avatar_location,  " +
				" t1.avatar_type,  " +
				" t2.name,  " +
				" t2.member_group_id,  " +
				" t2.posts,  " +
				" t3.field_6,  " +
				" t2.member_id,  " +
				" n1.id,  " +
				" n3.id " +
				" from " + getDbBoard() + "." + getDbPrefix() + "profile_portal t1 " +
				" inner join " + getDbBoard() + "." + getDbPrefix() + "members t2 on t2.member_id=t1.pp_member_id " +
				" inner join " + getDbBoard() + "." + getDbPrefix() + "pfields_content t3 on t2.member_id=t3.member_id " +
				" inner join " + getDbNetworkUserService() + "." +  "User n1 on t2.member_id=n1.externalId " +
				" inner join " + getDbNetworkUserService() + "." +  "UserAttribute n2 on n1.id=n2.user_id " +
				" inner join " + getDbNetWorkWalletService() + "." +  "Account n3 on n1.id=n3.userId " +
				" where  " +
				(useExternalId ? " n1.id= ? " : " n1.externalId= ? ") +
				" and  " +
				" n3.name = n2.value ";
				

	
			} else {// phototype = custom
				
				query = "select " +
				" t2.members_seo_name, " +
				" t2.pp_cover_photo, " +
				" t2.pp_photo_type,    " +
				" t2.pp_gravatar,    " +
				" t2.pp_main_photo,    " +
				" t2.fb_photo_thumb,   " + 
				" t2.fb_photo,    " +
				" t2.tc_photo,    " +
				" t2.pp_photo_type,    " +
				" t2.name,    " +
				" t2.member_group_id,    " +
				" t2.member_posts,    " +
				" t2.member_id,    " +
				" n1.id,    " +
				" n3.id   " +
				" from " + getDbBoard() + "." + getDbPrefix() + "core_members t2   " +
				" inner join " + getDbBoard() + "." + getDbPrefix() + "core_pfields_content t3 on t2.member_id=t3.member_id   " +
				" inner join " + getDbNetworkUserService() + "." +  "User n1 on t2.member_id=n1.externalId   " +
				" inner join " + getDbNetworkUserService() + "." +  "UserAttribute n2 on n1.id=n2.user_id   " +
				" inner join " + getDbNetWorkWalletService() + "." +  "Account n3 on n1.id=n3.userId   " +
				" where  " +
				(useExternalId ? " n1.id= ? " : " n1.externalId= ? ") +
				" and  " +
				" n3.name = n2.value ";
				
			
			}
		}
		
	log.warn("Query " + query);
	log.warn("User id to query " + (useExternalId ? externalId : id));
		//log.debug("Inside selectPlayerProfile : query " + query);
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
				String avatar_type = "";
				if (oldIPBVersion) {
					playerProfile.setId(resultSet.getInt("pp_member_id"));
					avatar_location = resultSet.getString("avatar_location");
					avatar_type = resultSet.getString("avatar_type");
					log.warn("avatar_type = "  + avatar_type);
					
					if (avatar_type != null && avatar_type.equals("upload")) {
						avatar_location = getSiteUrl() + "/uploads/" + avatar_location;
	//					log.debug("avatar_location = "  + avatar_location);
					} else if (avatar_type != null && avatar_type.equals("url")) {
						// nothing to do
					}
				} else {
					if (!newIPB4Version) {
	
						log.warn("Retrieving resultset ");
						playerProfile.setId(resultSet.getInt("t1.pp_member_id"));
						avatar_type = resultSet.getString("t1.pp_photo_type");
						String imageUrl = resultSet.getString("t1.pp_main_photo");
						String gravatarEmail = resultSet.getString("t1.pp_gravatar");
						String location = resultSet.getString("t3.field_6");
						log.warn("avatar_type : " + avatar_type);
						log.warn("imageUrl : " + imageUrl);
						log.warn("gravatarEmail : " + gravatarEmail);
						log.warn("location : " + location);
						playerProfile.setLocation(location);
						if (avatar_type != null && !avatar_type.equals("") && avatar_type.equals("gravatar")) {

							playerProfile.setPosts(resultSet.getInt("t2.posts"));
							playerProfile.setAvatar_type(avatar_type);
							
						} else {
							if (imageUrl != null && !imageUrl.equals("")) {
								avatar_location = getSiteUrl() + "/uploads" + "/" + imageUrl;
							} else {
								avatar_location = getSiteUrl() + "/uploads" + "/" + "novatar.png";
							}
							playerProfile.setPosts(resultSet.getInt("t2.member_posts"));
						}						
					} else {
						playerProfile.setId(resultSet.getInt("t2.member_id"));
						String imageUrl = resultSet.getString("t2.pp_main_photo");
						avatar_location = imageUrl;
					}
				}

				
				log.warn("url " + avatar_location);
				
				String name = resultSet.getString("t2.name");
				String nickName = resultSet.getString("t2.members_seo_name");
				int groupId = resultSet.getInt("t2.member_group_id");
				playerProfile.setGroupId(groupId);
				playerProfile.setVip((groupId == 7) ? true : false);
				log.warn("name " + name);
				playerProfile.setName(name);

				
				playerProfile.setAvatar_location(avatar_location);

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
