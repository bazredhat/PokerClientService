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

package com.board.games.service.wallet;

import org.apache.log4j.Logger;

import com.cubeia.backoffice.accounting.api.AccountingService;
import com.cubeia.backoffice.users.api.dto.User;
import com.cubeia.backoffice.users.client.UserServiceClient;
import com.cubeia.backoffice.users.client.UserServiceClientHTTP;
import com.cubeia.backoffice.wallet.api.dto.AccountBalanceResult;
import com.cubeia.backoffice.wallet.client.WalletServiceClient;
import com.cubeia.backoffice.wallet.client.WalletServiceClientHTTP;
public class WalletAdapter {
	private Logger log = Logger.getLogger(getClass());
	
	private  static UserServiceClient userService;
	private static AccountingService accountService;
	private WalletServiceClient walletService;

	private static final String EXTERNAL_USERNAME_ATTRIBUTE = "externalUsername";
	private static final String LEVEL = "level";

	private String userServiceUrl = "http://localhost:8080/user-service-rest/rest";
	
	private String walletServiceUrl = "http://localhost:8080/wallet-service-rest/rest";

	public WalletAdapter() {
		log.warn("Instantiate walletservice");
		walletService = new WalletServiceClientHTTP(walletServiceUrl);
		log.warn("instantiate userService");
		userService = new UserServiceClientHTTP(userServiceUrl);
		
	}

	public  String getSocialAvatar(long id) {
		log.warn("Inside  getSocialAvatar");
			User user =  userService.getUserById(id);
        if (user != null ) {
			String value = user.getAttributes().get("FB_SOCIAL_AVATAR_URL");
			if (value == null || (value != null && !value.equals("UNUSED"))) {
				return value;
			}	
        }
		return "";
	}
	public int getUserExternalId(String userId) {
		User user = userService.getUserById(new Long(userId));
		if (user!=null) {
			String extId = user.getExternalUserId();
			return new Integer(extId);
		}
		return -1;
	}

	public int getUserLevel(Long id) {
		try {
		User user = userService.getUserById(id);
		String level = user.getAttributes().get(LEVEL);
		log.warn("level : " + level + " on user id as #"  + id);
		return  Integer.parseInt(level);
		} catch (Exception e) {
			log.error("level is null");
		}
		return 0;
	}
	public AccountBalanceResult getAccountBalance(Long id) {
		try {
/*		log.warn("before call getAccountBalance for player " + id);
		User user = userService.getUserById(id);
		log.warn("getUserId : " + user.getUserId());
		log.warn("getUserName : " + user.getUserName());
		log.warn("getExternalUserId : " + user.getExternalUserId());
		String userName = user.getAttributes().get(EXTERNAL_USERNAME_ATTRIBUTE);
		log.warn("getUserName : " + userName);
		
*/		return  walletService.getAccountBalance(id);
		} catch (Exception e) {
			log.error("AccountBalanceResult is null");
		}
		return null;
	}
	

}
