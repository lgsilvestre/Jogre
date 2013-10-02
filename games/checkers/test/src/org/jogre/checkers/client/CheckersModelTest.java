/*
 * JOGRE (Java Online Gaming Real-time Engine) - Checkers
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
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
package org.jogre.checkers.client;

import junit.framework.TestCase;

/**
 * Checkers model unit test.
 */
public class CheckersModelTest extends TestCase {

    private CheckersModel model;
    private CheckersPieceMover pieceMover;

    /** Constructs test case with given name */
    public CheckersModelTest(String name) {
        super(name);
    }

    /** Initialization code */
    protected void setUp() {
        model = new CheckersModel();
        pieceMover = model.getPieceMover();
    }

    /** Cleanup code */
    protected void tearDown() {
        model.reset();
    }

    public void testStartPosition() throws Exception {
        int x, y;

        // Black pieces on top of board
        for (y = 0; y < 3; y++) {
            for (x = (y+1)%2; x < 8; x += 2) {
                assertEquals("Black pieces should be on top of board",
                    model.getPiece(x, y), ICheckersModel.B_NORMAL);
            }
        }

        // Empty pieces on dark squares
        for (y = 3; y < 5; y++) {
            for (x = (y+1)%2; x < 8; x += 2) {
                assertEquals("Empty squares should be in middle of board",
                    model.getPiece(x, y), ICheckersModel.EMPTY);
            }
        }

        // White pieces on bottom of board
        for (y = 5; y < 8; y++) {
            for (x = (y+1)%2; x < 8; x += 2) {
                assertEquals("White pieces should be on top of board",
                    model.getPiece(x, y), ICheckersModel.W_NORMAL);
            }
        }

        return;
    }

    /**
     * Test multiple capture.
     *
     * @throws Exception
     */
    public void testMultiCapture() throws Exception {
        // white: c3-b4
        executeMove('c', 3, 'b', 4);

        // black: b6-c5
        executeMove('b', 6, 'c', 5);

        // white: d2-c3
        executeMove('d', 2, 'c', 3);

        // black: f6-g5
        executeMove('f', 6, 'g', 5);

        // white: g3-h4
        executeMove('g', 3, 'h', 4);

        // black: d6-e5
        executeMove('d', 6, 'e', 5);

        // test: white should have 2 attacking moves now
        assertEquals("White should have 2 attacking moves", pieceMover.countPossibleAttackingMoves(ICheckersModel.PLAYER_ONE), 2);

        // white: execute two capture moves in sequence
        executeMove('b', 4, 'd', 6);

        // white: second capture move should fail by rules
        //executeMoveFail("Should not allow capture moves with different pieces", 'h', 4, 'f', 6);
    }

    private void executeMove(char x1, int y1, char x2, int y2) {
        assertTrue(model.executeMove(x1 - 'a', 8 - y1, x2 - 'a', 8 - y2));
    }

    private void executeMoveFail(String s, char x1, int y1, char x2, int y2) {
        assertFalse(s, model.executeMove(x1 - 'a', 8 - y1, x2 - 'a', 8 - y2));
    }

    /** Run test case */
    public static void main(String[] args) {
        String[] names = { CheckersModelTest.class.getName() };
        junit.textui.TestRunner.main(names);
    }
}
