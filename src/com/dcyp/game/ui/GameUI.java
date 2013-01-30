package com.dcyp.game.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.cyp.chess.chessboard.ChessboardController;
import com.cyp.chess.chessboard.ChessboardMode;
import com.cyp.chess.chessboard.ChessboardStatus;
import com.cyp.chess.chessboard.ChessboardUIInterface;
import com.cyp.chess.game.ChessGame;
import com.cyp.chess.game.ChessGameListener;
import com.cyp.chess.model.Move;
import com.cyp.chess.model.Position;
import com.cyp.chess.model.pgn.HTMLPGNText;
import com.cyp.chess.model.pgn.PGNOptions;
import com.cyp.game.IGameCommand;
import com.dcyp.chessboard.ChessBoardPainter;

public class GameUI extends JPanel implements ChessboardUIInterface , ChessGameListener{

	private static final long serialVersionUID = 1L;
	
	private ChessGame game;
	
	private ChessBoardPainter cbp;

	private ChessboardController ctrl;

	private PGNOptions pgnOption = new PGNOptions();

	private HTMLPGNText pgnTextView;

	private JButton abortButton;

	private JButton exitButton;

	private JButton drawButton;

	private JButton resignButton;

	private JButton rematchButton;

	private JButton sendChatButton;

	private JTextField chatMessageField;

	private JScrollPane moveListScroll;

	private JScrollPane chatListScroll;

	private JTextPane moveListArea;

	private JTextPane chatArea;

	private JTabbedPane tp;
	
	public GameUI(ChessGame game) {
		this.game = game;
		this.cbp = new ChessBoardPainter();
		this.pgnOption = new PGNOptions();
		this.pgnTextView = new HTMLPGNText(pgnOption);
		this.ctrl = new ChessboardController(this, pgnTextView, pgnOption);
		this.initUI();
		this.installListeners();		
		ctrl.newGame(new ChessboardMode(game.getChallenge().isReceived() ? ChessboardMode.TWO_PLAYERS_WHITE_REMOTE : ChessboardMode.TWO_PLAYERS_BLACK_REMOTE));
		cbp.setFlipped(game.getChallenge().isReceived());
		ctrl.startGame();
	}

	public ChessboardController getController() {
		return this.ctrl;
	}

	@Override
	public void setPosition(Position pos, String variantInfo,
			ArrayList<Move> variantMoves) {
		cbp.setPosition(pos);
	}

	@Override
	public void setSelection(int sq) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveListUpdated() {
		this.moveListArea.setText(this.pgnTextView.getHTMLData());

	}

	@Override
	public void requestPromotePiece() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Object[] options = { "Queen", "Rook", "Bishop", "Knight" };
				int choice = JOptionPane
						.showOptionDialog(cbp, "Promote pawn to?",
								"Pawn Promotion", 0,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				ctrl.reportPromotePiece(choice);
			}
		});
	}

	@Override
	public void runOnUIThread(Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	@Override
	public void reportInvalidMove(Move m) {

	}

	@Override
	public void setRemainingTime(long wTime, long bTime, long nextUpdate) {

	}

	@Override
	public void setAnimMove(Position sourcePos, Move move, boolean forward) {

	}

	@Override
	public String whitePlayerName() {
		return this.game.getWhitePlayer();
	}

	@Override
	public String blackPlayerName() {
		return this.game.getBlackPlayer();
	}

	@Override
	public boolean discardVariations() {
		return false;
	}

	@Override
	public void remoteMoveMade() {

	}

	private void initUI() {
		this.resignButton = new JButton("Resign");
		this.exitButton = new JButton("Exit");
		this.drawButton = new JButton("Draw");
		this.abortButton = new JButton("Abort");
		this.rematchButton = new JButton("Rematch");
		this.moveListArea = new JTextPane();
		this.moveListScroll = new JScrollPane(this.moveListArea);
		this.chatArea = new JTextPane();
		this.chatListScroll = new JScrollPane(this.chatArea);
		this.chatMessageField = new JTextField();
		this.sendChatButton = new JButton("Send");
		this.setLayout(new BorderLayout());
		JPanel eastPanel = new JPanel(new BorderLayout());
		this.add(this.cbp, BorderLayout.CENTER);
		this.add(eastPanel, BorderLayout.EAST);
		JPanel eastNorthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		eastNorthPanel.add(this.abortButton);
		eastNorthPanel.add(this.resignButton);
		eastNorthPanel.add(this.drawButton);
		eastNorthPanel.add(this.rematchButton);
		eastNorthPanel.add(this.exitButton);
		eastPanel.add(eastNorthPanel, BorderLayout.NORTH);
		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.add(this.chatListScroll, BorderLayout.CENTER);
		JPanel chatSountPanel = new JPanel(new BorderLayout());
		chatSountPanel.add(this.chatMessageField, BorderLayout.CENTER);
		chatSountPanel.add(this.sendChatButton, BorderLayout.EAST);
		chatPanel.add(chatSountPanel, BorderLayout.SOUTH);
		this.tp = new JTabbedPane();
		this.tp.add("Moves", this.moveListScroll);
		this.tp.add("Chat", chatPanel);

		eastPanel.add(this.tp, BorderLayout.CENTER);
	}

	private void installListeners() {
		this.game.addGameListener(this);
		
		cbp.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (ctrl.localTurn()) {
					int sq = cbp.eventToSquare(evt);
					Move m = cbp.mousePressed(sq);
					if (m != null) {
						ctrl.makeLocalMove(m);
						try {
							game.sendMove(m);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}				
				
			}

			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if (ctrl.localTurn()) {
					int sq = cbp.eventToSquare(evt);
					Move m = cbp.mouseReleased(sq);
					if (m != null) {
						ctrl.makeLocalMove(m);
						try {
							game.sendMove(m);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		cbp.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				if (ctrl.localTurn()) {
					cbp.mouseDragged(evt);
				}
			}
		});
	}

	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		JFrame fr = new JFrame();
		fr.setContentPane(new GameUI(null));
		fr.pack();
		fr.setVisible(true);
		fr.setDefaultCloseOperation(3);
	}

	@Override
	public void setStatus(ChessboardStatus status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void localMoveMade(Move m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commandReceived(IGameCommand command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readyReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resignReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chatReceived(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveReceived(final String move) {
		runOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				ctrl.makeRemoteMove(move);				
			}
		});		
	}

	@Override
	public void drawRequestReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawAcceptedReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abortRequestReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abortAcceptedReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rematchRequestReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rematchAcceptedReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameClosedReceived() {
		// TODO Auto-generated method stub
		
	}
}
