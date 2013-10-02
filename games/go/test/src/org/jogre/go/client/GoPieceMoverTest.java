/*
 * JOGRE (Java Online Gaming Real-time Engine) - Go
 * Copyright (C) 2005  Bob Marks (marksie531@yahoo.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.go.client;


import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * Test case which test the rules of the game of Go.
 *
 * Used the following website as a guide to the rules
 * http://gobase.org/studying/rules/?id=0&ln=uk
 *
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoPieceMoverTest extends TestCase {

    // declare model and piece mover.
    private GoModel model;
    private GoPieceMover pieceMover;

    private static final int B = GoModel.BLACK;
    private static final int W = GoModel.WHITE;

    // Games from http://playgo.to/interactive/
    private static final String GAME1 =
        "D5 F5 D7 F3 D3 F7 F8 G8 E8 D2 " +
        "C2 E3 D4 H7 E2 F2 D1 E7 F1 G1 " +
        "E1 G2 E5 E6 D6 F9 D9 E4 E9 G9 ";
    private static final String GAME2 =
        "E5 E4 D4 D5 C4 D6 E6 D7 F4 E3 " +
        "F3 E2 C2 F5 F6 G5 G6 G4 G3 H3 " +
        "B6 F8 H5 H4 G8 F7 G7 B7 C7 C6 " +
        "C8 B5 E7 D8 E8 B8 B4 H6 H7 I5 " +
        "D2 I7 I8 I6 C5 A6 E1 F1 D1 F2 " +
        "D3 G2 A4 E9 F9 D9 A5 B6";

    /** Constructs test case with given name */
    public GoPieceMoverTest(String name) {
        super(name);
    }

    /** Initialization code */
    protected void setUp() {
        model      = new GoModel();
        pieceMover = new GoPieceMover (model);
    }

    /**
     * Test basic moves.
     */
    public void testBasicMoves () {

        model.setNumOfCells(19); model.reset ();

        // Test basic movement with no captures
        assertTrue  (pieceMover.isValidMove (5, 5, B));
        pieceMover.move (5, 5, B);
        assertFalse (pieceMover.isValidMove (5, 5, W));

        assertTrue  (pieceMover.isValidMove (10, 10, W));
        pieceMover.move (10, 10, W);
        assertFalse (pieceMover.isValidMove (10, 10, W));
    }

    /**
     * Test groups are coming back correctly.
     */
    public void testGroups () {
        String [] pieces = {"    b   w",        // b = black, w = white
                            "bwb  b bw",
                            "b b  bb  ",
                            "bw b   bb",
                            "bbb  bwww",
                            " b  bbw w"};
        setupBoard (9, pieces);

        // Check 1st row
        assertEquals (1, pieceMover.getPlayerGroup (4, 0).size());
        assertEquals (4, pieceMover.getPlayerGroup (7, 0).size());
        assertEquals (2, pieceMover.getPlayerGroup (8, 0).size());

        // random checks
        Vector v;
        v = pieceMover.getPlayerGroup (0, 1);
        assertEquals (7, v.size());
        int [] expectedIndexes = {9, 18, 27, 36, 37, 38, 46};
        assertTrue (vectorContains (v, expectedIndexes));

        Vector whiteGroups = pieceMover.getPlayerGroups(W);
        assertEquals (4, whiteGroups.size());
        Vector blackGroups = pieceMover.getPlayerGroups(B);
        assertEquals (8, blackGroups.size());

        // Check white groups only
        expectedIndexes = new int [] {8, 17};
        assertTrue (vectorContains ((Vector)whiteGroups.get(0), expectedIndexes));
        expectedIndexes = new int [] {10};
        assertTrue (vectorContains ((Vector)whiteGroups.get(1), expectedIndexes));
        expectedIndexes = new int [] {28};
        assertTrue (vectorContains ((Vector)whiteGroups.get(2), expectedIndexes));
        expectedIndexes = new int [] {42, 43, 44, 51, 53};
        assertTrue (vectorContains ((Vector)whiteGroups.get(3), expectedIndexes));
    }

    /**
     * Test capturing stones.
     */
    public void testBasicCapture () {
        // Test a basic capture
        String [] pieces = {"   ",
                            "bwb",
                            " b "};
        setupBoard (3, pieces);

        assertEquals (W, model.getData(1, 1));
        assertEquals (0, model.getCapturedStones (B));
        assertEquals (0, model.getCapturedStones (W));

        // Check valid move for both players, then take it for black
        assertTrue  (pieceMover.isValidMove (1, 0, W));
        assertTrue  (pieceMover.isValidMove (1, 0, B));
        pieceMover.move (1, 0, B);

        assertEquals (1, model.getCapturedStones (W));
        assertEquals (0, model.getCapturedStones (B));
        assertEquals (GoModel.BLANK, model.getData(1, 1));
    }

    /**
     * Test more complex capture.
     */
    public void testComplexCapture () {
        String [] pieces = {"     ",
                            "  b  ",
                            " bwwb",
                            " bwb ",
                            "  b  "};
        setupBoard (5, pieces);

        // Check valid move for both players then using black take it
        assertTrue  (pieceMover.isValidMove (3, 1, W));
        assertTrue  (pieceMover.isValidMove (3, 1, B));
        pieceMover.move (3, 1, B);

        assertEquals (3, model.getCapturedStones (W));
        assertEquals (0, model.getCapturedStones (B));
        assertEquals (GoModel.BLANK, model.getData(2, 2));
        assertEquals (GoModel.BLANK, model.getData(2, 3));
        assertEquals (GoModel.BLANK, model.getData(3, 2));
    }

    /**
     * Test more complex capture.
     */
    public void testMoreComplexCapture () {
        String [] pieces = {"       ",
                            " w     ",
                            "wbww   ",
                            "wbbbw  ",
                            "wb bw  ",
                            "wbbw   ",
                            " ww    "};
        setupBoard (7, pieces);

        // Check valid move for both players then using black take it
        assertEquals (GoModel.BLANK, model.getData(2, 4));
        assertFalse (pieceMover.isValidMove (2, 4, B));
        assertTrue (pieceMover.isValidMove (2, 4, W));
        pieceMover.move (2, 4, W);

        assertEquals (8, model.getCapturedStones (B));
        assertEquals (0, model.getCapturedStones (W));
        assertEquals (GoModel.BLANK, model.getData(1, 2));
        assertEquals (GoModel.BLANK, model.getData(1, 3));
        assertEquals (GoModel.BLANK, model.getData(1, 4));
    }

    /**
     * Test suicide.
     */
    public void testSuicide () {
        String [] pieces = {" b ",
                            "b b",
                            " b "};
        setupBoard (3, pieces);

        assertFalse (pieceMover.isValidMove (1, 1, W));
    }

    /**
     * Test the KO rule.
     */
    public void testKoRule () {
        String [] pieces = {" bw ",
                            "bw w",
                            " bw "};
        setupBoard (4, pieces);

        assertTrue  (pieceMover.isValidMove (2, 1, B));
        pieceMover.move (2, 1, B);
        assertEquals (GoModel.BLANK, model.getData(1,1));

        // Assert next move is false as creates KO (original position).
        assertFalse (pieceMover.isValidMove(1, 1, W));
    }

    /**
     * Test mark dead.
     */
    public void testMarkDead () {
        String [] pieces = {"wwbw ",
                            "w bw ",
                            "bbbw ",
                            "  bw ",
                            "  bw "};
        setupBoard (5, pieces);
        assertEquals (GoModel.WHITE, model.getData(0, 0));
        assertEquals (GoModel.WHITE, model.getData(1, 0));
        assertEquals (GoModel.WHITE, model.getData(0, 1));

        // Mark left hand corner as dead
        pieceMover.mark (0,0);
        assertEquals (GoModel.WHITE_MARKED_DEAD, model.getData(0, 0));
        assertEquals (GoModel.WHITE_MARKED_DEAD, model.getData(1, 0));
        assertEquals (GoModel.WHITE_MARKED_DEAD, model.getData(0, 1));

        // If a stone is marked which is already dead it should return it
        // to alive again
        pieceMover.mark (0,0);
        assertEquals (GoModel.WHITE, model.getData(0, 0));
        assertEquals (GoModel.WHITE, model.getData(1, 0));
        assertEquals (GoModel.WHITE, model.getData(0, 1));
    }

    /**
     * Test simple score.
     */
    public void testScore1 () {
        String [] pieces = {"  bw ",
                            "  bw ",
                            "  bw ",
                            "  bw ",
                            "  bw "};
        setupBoard (5, pieces);
        model.setKomi (0);          // set komi to zero

        GoScore score = pieceMover.getScore ();

        // Test scoring method.
        assertEquals (15, score.getArea (B));
        assertEquals (10, score.getArea (W));
        assertEquals (10, score.getTerritory (B));
        assertEquals (5,  score.getTerritory (W));

        // Final score
        assertEquals (B, score.getWinningPlayer());
        assertEquals (5, score.getWinningScore());
    }

    /**
     * Test simple score.
     */
    public void testScore2 () {
        String [] pieces = {" bww ",
                            " b w ",
                            " b w ",
                            " bbw ",
                            "  bw "};
        setupBoard (5, pieces);
        model.setKomi (0);          // set komi to zero

        GoScore score = pieceMover.getScore ();

        // Test scoring method.
        assertEquals (12, score.getArea (GoModel.BLACK));
        assertEquals (11, score.getArea (GoModel.WHITE));
        assertEquals (6,  score.getTerritory (GoModel.BLACK));
        assertEquals (5,  score.getTerritory (GoModel.WHITE));

        // Final score
        assertEquals (B, score.getWinningPlayer());
        assertEquals (1, score.getWinningScore());
    }

    /**
     * Test simple score 3.
     */
    public void testScore3 () {
        String [] pieces = {"wb   ",
                            " wb  ",
                            "  wbb",
                            "  www",
                            "  w b"};
        setupBoard (5, pieces);
        model.setKomi (0);          // set komi to zero

        model.setScoreMethod(GoScore.SCORE_METHOD_AREA);
        GoScore score = pieceMover.getScore ();

        // Test scoring method.
        assertEquals (10, score.getArea      (GoModel.BLACK));
        assertEquals (14, score.getArea      (GoModel.WHITE));
        assertEquals (5,  score.getTerritory (GoModel.BLACK));
        assertEquals (7,  score.getTerritory (GoModel.WHITE));
        assertEquals (0,  score.getPrisoner  (GoModel.BLACK));
        assertEquals (0,  score.getPrisoner  (GoModel.WHITE));

        // Final score (area score method)
        assertEquals (W, score.getWinningPlayer());
        assertEquals (4, score.getWinningScore());

        // Final score (territory score method)
        model.setScoreMethod(GoScore.SCORE_METHOD_TERRITORY);
        score = pieceMover.getScore ();
        assertEquals (W, score.getWinningPlayer());
        assertEquals (2, score.getWinningScore());

        // Make black piece as and make sure correct score is shown
        pieceMover.mark (4, 4);
        score = pieceMover.getScore ();     // refresh score
        assertEquals (9,  score.getArea      (GoModel.BLACK));
        assertEquals (16, score.getArea      (GoModel.WHITE));
        assertEquals (5,  score.getTerritory (GoModel.BLACK));
        assertEquals (9,  score.getTerritory (GoModel.WHITE));
        assertEquals (0,  score.getPrisoner  (GoModel.BLACK));
        assertEquals (1,  score.getPrisoner  (GoModel.WHITE));

        // Final score (area score method)
        model.setScoreMethod(GoScore.SCORE_METHOD_AREA);
        score = pieceMover.getScore ();     // refresh score
        assertEquals (W, score.getWinningPlayer());
        assertEquals (7, score.getWinningScore());

        // Final score (territory score method)
        model.setScoreMethod(GoScore.SCORE_METHOD_TERRITORY);
        score = pieceMover.getScore ();     // refresh score
        assertEquals (W, score.getWinningPlayer());
        assertEquals (5, score.getWinningScore());
    }

    /**
     * Test simple score 3.
     */
    public void testScore4 () {
        String [] pieces = {"  wb ",
                            "b wb ",
                            "b wbw",
                            "  wb ",
                            "  wb "};
        setupBoard (5, pieces);
        model.setKomi (0);          // set komi to zero

        model.setScoreMethod(GoScore.SCORE_METHOD_AREA);
        GoScore score = pieceMover.getScore ();

        // Test scoring method.
        assertEquals (7, score.getArea      (GoModel.BLACK));
        assertEquals (6, score.getArea      (GoModel.WHITE));
        assertEquals (0, score.getTerritory (GoModel.BLACK));
        assertEquals (0, score.getTerritory (GoModel.WHITE));
        assertEquals (0, score.getPrisoner  (GoModel.BLACK));
        assertEquals (0, score.getPrisoner  (GoModel.WHITE));

        // Final score (area score method)
        assertEquals (B, score.getWinningPlayer());
        assertEquals (1, score.getWinningScore());

        // Final score (territory score method)
        model.setScoreMethod (GoScore.SCORE_METHOD_TERRITORY);
        score = pieceMover.getScore ();
        assertEquals (0, score.getWinningScore());

        // Make black piece as and make sure correct score is shown
        pieceMover.mark (0, 2); pieceMover.mark (4, 2);
        score = pieceMover.getScore ();     // refresh score
        assertEquals (10,  score.getArea      (GoModel.BLACK));
        assertEquals (15, score.getArea       (GoModel.WHITE));
        assertEquals (5,  score.getTerritory  (GoModel.BLACK));
        assertEquals (10,  score.getTerritory (GoModel.WHITE));
        assertEquals (1,  score.getPrisoner   (GoModel.BLACK));
        assertEquals (2,  score.getPrisoner   (GoModel.WHITE));

        // Final score (area score method)
        model.setScoreMethod(GoScore.SCORE_METHOD_AREA);
        score = pieceMover.getScore ();     // refresh score
        assertEquals (W, score.getWinningPlayer());
        assertEquals (5, score.getWinningScore());

        // Final score (territory score method)
        model.setScoreMethod(GoScore.SCORE_METHOD_TERRITORY);
        score = pieceMover.getScore ();     // refresh score
        assertEquals (W, score.getWinningPlayer());
        assertEquals (6, score.getWinningScore());
    }


    /**
     * Test the score.
     */
    public void testComplexScore () {
        String [] pieces = {"wbb    ",
                            "wwbb   ",
                            " wwbb  ",
                            "  wb   ",
                            "  wwb  ",
                            "   wb b",
                            "   wwb "};
        setupBoard (7, pieces);
        model.setKomi(0);           // set komi to zero

        GoScore score = pieceMover.getScore ();
        assertEquals (27, score.getArea      (GoModel.BLACK));
        assertEquals (22, score.getArea      (GoModel.WHITE));
        assertEquals (16, score.getTerritory (GoModel.BLACK));
        assertEquals (11, score.getTerritory (GoModel.WHITE));
    }

    /**
     * Play game 1.
     */
    public void testPlayGame1 () {
        model.setNumOfCells(9); model.reset();
        model.setKomi (0);
        playGame (GAME1);

        GoScore score = pieceMover.getScore();
        assertEquals (28, score.getTerritory (GoModel.BLACK));
        assertEquals (24, score.getTerritory (GoModel.WHITE));
        assertEquals (1,  score.getPrisoner (GoModel.BLACK));
        assertEquals (0,  score.getPrisoner (GoModel.WHITE));
        assertEquals (5,  score.getWinningScore());
        assertEquals (GoModel.BLACK,  score.getWinningPlayer());
    }

    /**
     * Play game 2.
     */
    public void testPlayGame2 () {
        model.setNumOfCells(9); model.reset ();
        model.setKomi (0);
        playGame (GAME2);

        // Set black dead stones
        pieceMover.mark(2, 1);

        GoScore score = pieceMover.getScore();
        assertEquals (36, score.getArea (GoModel.BLACK));
        assertEquals (45, score.getArea (GoModel.WHITE));
        assertEquals (14, score.getTerritory (GoModel.BLACK));
        assertEquals (18, score.getTerritory (GoModel.WHITE));
        assertEquals (2, score.getPrisoner (GoModel.BLACK));
        assertEquals (7, score.getPrisoner (GoModel.WHITE));
        assertEquals (9,  score.getWinningScore());
        assertEquals (GoModel.WHITE, score.getWinningPlayer());
    }

    /**
     * Play a game using information from String.
     *
     * @param str
     */
    private void playGame (String gameStr) {
        StringTokenizer st = new StringTokenizer (gameStr, " ");
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        int index = 1;
        int player = GoModel.BLACK;
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            int x = chars.indexOf(str.charAt (0));
            int y = model.getNumOfCells() - Integer.parseInt(str.substring(1));

            pieceMover.move (x, y, player);
            player = player == B ? W : B;
            index ++;
        }
    }

    /**
     * Helper method to setup the board.
     *
     * @param numOfCells  Number of cells
     * @param strArray    Array of Strings
     */
    private void setupBoard (int numOfCells, String [] strArray) {
        model.setNumOfCells(numOfCells); model.reset ();

        for (int y = 0; y < strArray.length; y++) {
            String str = strArray[y];

            for (int x = 0; x < str.length(); x++) {
                char c = str.charAt(x);
                if (c == 'b')
                    model.setData(x, y, B);
                else if (c == 'w')
                    model.setData(x, y, W);
            }
        }
    }

    /**
     * Ensure vector contains correct attributes.
     * @return
     */
    private boolean vectorContains (Vector v, int [] array) {
        for (int i = 0; i < array.length; i++)
            if (!v.contains(new Integer(array[i])))
                return false;

        return true;
    }
}