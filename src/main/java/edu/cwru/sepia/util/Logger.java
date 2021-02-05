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
package edu.cwru.sepia.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
/**
 * A utility class for logging errors. Only has a single instance, which by default 
 * sends messages to {@link System#err}. A different target may be provided using
 * {@link #open(String)} or {@link #open(OutputStream)}.
 * @author Tim
 *
 */
public class Logger {
	private static Logger instance;	
	public static Logger getInstance() {
		if(instance == null)
			instance = new Logger();
		return instance;
	}
	
	private PrintStream out;
	private Logger() {
		out = System.err;
	}
	/**
	 * Attempts to redirect messages to a file.
	 * @param filename - the name of the file in which to store messages
	 * @return whether the file was successfully opened
	 */
	public boolean open(String filename) {
		if(out != System.err && out != System.out)
			out.close();
		try 
		{
			out = new PrintStream(new File(filename));
		} 
		catch (Exception ex) 
		{
			return false;
		}
		return true;
	}
	/**
	 * Attempts to redirect messages to an output stream.
	 * @param stream - the name of the stream to which to send messages
	 * @return whether the stream was successfully opened
	 */
	public boolean open(OutputStream stream) {
		if(out != System.err && out != System.out)
			out.close();
		try 
		{
			out = new PrintStream(stream);
		} 
		catch (Exception ex) 
		{
			return false;
		}
		return true;
	}
	/**
	 * Closes the currently opened file or stream and reverts to logging to {@link System#err}.
	 */
	public void close() {
		if(out == System.err)
			return;
		out.close();
		out = System.err;
	}
	/**
	 * Records a message to the currently opened stream or file.
	 * @param message
	 * @param level - the level of urgency of the message
	 */
	public void recordMessage(String message, MessageLevel level) {
		out.println(level+": "+message);
	}
	/**
	 * A list of different levels of urgency of messages
	 * @author Tim
	 *
	 */
	public static enum MessageLevel { OBSERVATION, WARNING, ERROR, CRITICAL_ERROR };
}
