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

package com.board.games.svc.game;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.board.games.model.Profile;
import com.board.games.svc.bean.GameConfigQuery;


@Path("/gameRestService")
@Produces({"application/json","application/xml"})
public class GameServiceImpl implements GameService {

	private static final Logger logger = Logger.getLogger(GameServiceImpl.class);

	private GameConfigQuery gameConfigQuery;

	public GameConfigQuery getGameConfigQuery() {
		return gameConfigQuery;
	}

	public void setGameConfigQuery(GameConfigQuery gameConfigQuery) {
		this.gameConfigQuery = gameConfigQuery;
	}


	@GET
	@Path("/public/player/{id}")	
	@Produces({"application/json","application/xml"})
	public Profile getProfile(@PathParam("id") String externalId) {
		logger.warn("Inside getProfile");
		GameConfigQuery gcq = getGameConfigQuery();
		try {
			gcq.initialize();
			return gcq.getProfile(0, externalId, 0 );
		} catch (Exception e) {
			logger.error("Exception in init " + e.toString());
		}	
		return null;
	}


}