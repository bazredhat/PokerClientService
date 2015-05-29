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

package com.board.games.svc.exception;

public class BusinessException extends Exception {

	public int errorCode;

	public BusinessException(String desc, int errorCode) {
		super(desc);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public BusinessException(Exception e) {
		super(e);
	}

	public BusinessException(String msg) {
		super(msg);
	}
}