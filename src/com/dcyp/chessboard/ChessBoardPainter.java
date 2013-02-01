package com.dcyp.chessboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.cyp.chess.model.Move;
import com.cyp.chess.model.Piece;
import com.cyp.chess.model.Position;

public class ChessBoardPainter extends JComponent {
	private static final long serialVersionUID = -1319250011487017825L;
	private Position pos;
	private int selectedSquare;
	private int x0, y0, sqSize;
	private boolean flipped;
	private Font chessFont;

	// For piece animation during dragging
	private int activeSquare;
	private boolean dragging;
	private int dragX;
	private int dragY;
	private boolean cancelSelection;
	private TexturePaint lightTP;
	private TexturePaint darkTP;

	public ChessBoardPainter() {
		pos = new Position();
		selectedSquare = -1;
		x0 = y0 = sqSize = 0;
		flipped = false;
		activeSquare = -1;

		try {
			BufferedImage lightBi = ImageIO.read(Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("resources/light.gif"));
			lightTP = new TexturePaint(lightBi, new Rectangle(0, 0,
					lightBi.getWidth(), lightBi.getWidth()));
			BufferedImage darkBi = ImageIO.read(Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("resources/dark.gif"));
			darkTP = new TexturePaint(darkBi, new Rectangle(0, 0,
					lightBi.getWidth(), lightBi.getWidth()));
			chessFont = Font.createFont(Font.TRUETYPE_FONT, Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("resources/ChessCases.ttf"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.setPreferredSize(new Dimension(200,200));
	}

	/**
	 * Set the board to a given state.
	 * 
	 * @param pos
	 */
	final public void setPosition(Position pos) {
		this.pos = pos;
		repaint();
	}

	/**
	 * Set/clear the board flipped status.
	 * 
	 * @param flipped
	 */
	final public void setFlipped(boolean flipped) {
		this.flipped = flipped;
		repaint();
	}

	/**
	 * Set/clear the selected square.
	 * 
	 * @param square
	 *            The square to select, or -1 to clear selection.
	 */
	final public void setSelection(int square) {
		if (square != this.selectedSquare) {
			this.selectedSquare = square;
			repaint();
		}
	}

	@Override
	public void paint(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		Dimension size = getSize();
		sqSize = (Math.min(size.width, size.height) - 4) / 8;
		x0 = (size.width - sqSize * 8) / 2;
		y0 = (size.height - sqSize * 8) / 2;

		boolean doDrag = (activeSquare >= 0) && dragging;

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				final int xCrd = getXCrd(x);
				final int yCrd = getYCrd(y);
				g.setPaint(Position.darkSquare(x, y) ? darkTP : lightTP);
				g.fillRect(xCrd, yCrd, sqSize, sqSize);

				int sq = Position.getSquare(x, y);
				int p = pos.getPiece(sq);
				if (doDrag && (sq == activeSquare)) {
					// Skip this piece. It will be drawn later at (dragX,dragY)
				} else {
					drawPiece(g, xCrd + sqSize / 2, yCrd + sqSize / 2, p);
				}
			}
		}
		if (selectedSquare >= 0) {
			int selX = Position.getX(selectedSquare);
			int selY = Position.getY(selectedSquare);
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(3));
			g.drawRect(getXCrd(selX), getYCrd(selY), sqSize, sqSize);
		}
		if (doDrag) {
			int p = pos.getPiece(activeSquare);
			drawPiece(g, dragX, dragY, p);
		}
	}

	private final void drawPiece(Graphics2D g, int xCrd, int yCrd, int p) {
		String psb, psw;
				
        switch (p) {
            default:
            case Piece.EMPTY:   psb = null; psw = null; break;
            case Piece.WKING:   psb = "H"; psw = "k"; break;
            case Piece.WQUEEN:  psb = "I"; psw = "l"; break;
            case Piece.WROOK:   psb = "J"; psw = "m"; break;
            case Piece.WBISHOP: psb = "K"; psw = "n"; break;
            case Piece.WKNIGHT: psb = "L"; psw = "o"; break;
            case Piece.WPAWN:   psb = "M"; psw = "p"; break;
            case Piece.BKING:   psb = "N"; psw = "q"; break;
            case Piece.BQUEEN:  psb = "O"; psw = "r"; break;
            case Piece.BROOK:   psb = "P"; psw = "s"; break;
            case Piece.BBISHOP: psb = "Q"; psw = "t"; break;
            case Piece.BKNIGHT: psb = "R"; psw = "u"; break;
            case Piece.BPAWN:   psb = "S"; psw = "v"; break;
        }
		
		
		if (psb != null) {
			FontRenderContext frc = g.getFontRenderContext();
			if (chessFont.getSize() != sqSize) {
				chessFont = chessFont.deriveFont((float) sqSize);
			}
			g.setFont(chessFont);
			Rectangle2D textRect = g.getFont().getStringBounds(psb, frc);
			int xCent = (int) textRect.getCenterX();
			int yCent = (int) textRect.getCenterY();
			g.setColor( Color.WHITE);			
			g.drawString(psw, xCrd - xCent, yCrd - yCent);
			g.setColor(Color.BLACK);			
			g.drawString(psb, xCrd - xCent, yCrd - yCent);			
		}
	}

	private final int getXCrd(int x) {
		return x0 + sqSize * (flipped ? 7 - x : x);
	}

	private final int getYCrd(int y) {
		return y0 + sqSize * (flipped ? y : (7 - y));
	}

	/**
	 * Compute the square corresponding to the coordinates of a mouse event.
	 * 
	 * @param evt
	 *            Details about the mouse event.
	 * @return The square corresponding to the mouse event, or -1 if outside
	 *         board.
	 */
	public final int eventToSquare(MouseEvent evt) {
		int xCrd = evt.getX();
		int yCrd = evt.getY();

		int sq = -1;
		if ((xCrd >= x0) && (yCrd >= y0) && (sqSize > 0)) {
			int x = (xCrd - x0) / sqSize;
			int y = 7 - (yCrd - y0) / sqSize;
			if ((x >= 0) && (x < 8) && (y >= 0) && (y < 8)) {
				if (flipped) {
					x = 7 - x;
					y = 7 - y;
				}
				sq = Position.getSquare(x, y);
			}
		}
		return sq;
	}

	public final Move mousePressed(int sq) {
		Move m = null;
		cancelSelection = false;
		int p = pos.getPiece(sq);
		if ((selectedSquare >= 0) && (sq == selectedSquare)) {
			int fromPiece = pos.getPiece(selectedSquare);
			if ((fromPiece == Piece.EMPTY)
					|| (Piece.isWhite(fromPiece) != pos.whiteMove)) {
				return m; // Can't move the piece the oppenent just moved.
			}
		}
		if ((selectedSquare < 0)
				&& ((p == Piece.EMPTY) || (Piece.isWhite(p) != pos.whiteMove))) {
			return m; // You must click on one of your own pieces.
		}
		activeSquare = sq;
		dragging = false;
		dragX = dragY = -1;

		if (selectedSquare >= 0) {
			if (sq == selectedSquare) {
				cancelSelection = true;
			} else {
				if ((p == Piece.EMPTY) || (Piece.isWhite(p) != pos.whiteMove)) {
					m = new Move(selectedSquare, sq, Piece.EMPTY);
					activeSquare = -1;
					setSelection(sq);
				}
			}
		}
		if (m == null) {
			setSelection(-1);
		}
		return m;
	}

	public final void mouseDragged(MouseEvent evt) {
		final int xCrd = evt.getX();
		final int yCrd = evt.getY();
		if (!dragging || (dragX != xCrd) || (dragY != yCrd)) {
			dragging = true;
			dragX = xCrd;
			dragY = yCrd;
			repaint();
		}
	}

	public final Move mouseReleased(int sq) {
		Move m = null;
		if (activeSquare >= 0) {
			if (sq != activeSquare) {
				m = new Move(activeSquare, sq, Piece.EMPTY);
				setSelection(sq);
			} else if (!cancelSelection) {
				setSelection(sq);
			}
			activeSquare = -1;
			repaint();
		}
		return m;
	}
}
