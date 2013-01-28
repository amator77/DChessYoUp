package com.chessyoup.online;

import com.chess.game.ChessGameController;
import com.chessyoup.ui.GameUI;
import com.gamelib.accounts.Account;
import com.gamelib.transport.Contact;

public class OnlineGame {
	
	public Account account;
	
	public Contact remoteContact;
	
	public GameUI gameUI;
	
	public ChessGameController ctrl;
	
	public OnlineGame(Account account,  Contact remoteContact ){
		this.account = account;
		this.remoteContact = remoteContact;
		this.gameUI = new GameUI();
	}
	
	public void showUI(){
		
	}
}
