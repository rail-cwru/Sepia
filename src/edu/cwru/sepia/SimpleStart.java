/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia;

import edu.cwru.sepia.start.StartListener;
import edu.cwru.sepia.start.StartWindow;

/**
 * An entry point to Sepia that allows for visual configuration of runtime parameters.
 * @author Gary
 *
 */
public final class SimpleStart {

    public static void main(String[] args) {

        StartWindow startWindow = new StartWindow();
        startWindow.addStartListener(new StartListener() {
            @Override
            public void start(String[] args) {
                new SimpleStartThread(args).start();
            }
        });

        startWindow.pack();
        startWindow.setVisible(true);

    }

    public static class SimpleStartThread extends Thread {

        private final String[] args;

        public SimpleStartThread(String[] args) {
            this.args = args;
        }

        @Override
        public void run() {
            try {
                Main.main(args);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    private SimpleStart() {}
}
