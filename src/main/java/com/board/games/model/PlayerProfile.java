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

package com.board.games.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PlayerProfile")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"id",
		"avatar_type",
    "avatar_location",
    "name",
    "posts",
    "groupId",    
    "isPremiumLevel1",    
    "isPremiumLevel2",    
    "isVip",    
    "location",    
    "balance",
    "level"
})

public class PlayerProfile {
	private int id = 0;
	private String avatar_location = "";
	private String avatar_type = "";
	private String name="";
	private int posts = 0;
	private int groupId = 0;
	private boolean isPremiumLevel1 = false;
	private boolean isPremiumLevel2 = false;
	private boolean isVip = false;
	private String location;
	private BigDecimal balance = new BigDecimal(0);
	private int level = 0;
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isVip() {
		return isVip;
	}
	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}
	public boolean isPremiumLevel1() {
		return isPremiumLevel1;
	}
	public void setPremiumLevel1(boolean isPremiumLevel1) {
		this.isPremiumLevel1 = isPremiumLevel1;
	}
	public boolean isPremiumLevel2() {
		return isPremiumLevel2;
	}
	public void setPremiumLevel2(boolean isPremiumLevel2) {
		this.isPremiumLevel2 = isPremiumLevel2;
	}
	public int getPosts() {
		return posts;
	}
	public void setPosts(int posts) {
		this.posts = posts;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAvatar_location() {
		return avatar_location;
	}
	public void setAvatar_location(String avatar_location) {
		this.avatar_location = avatar_location;
	}
	public String getAvatar_type() {
		return avatar_type;
	}
	public void setAvatar_type(String avatar_type) {
		this.avatar_type = avatar_type;
	}
}
