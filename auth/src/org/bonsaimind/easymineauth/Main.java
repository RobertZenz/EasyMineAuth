/*
 * Copyright 2013 Robert 'Bobby' Zenz. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Robert 'Bobby' Zenz ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Robert 'Bobby' Zenz.
 */
package org.bonsaimind.easymineauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bonsaimind.minecraftmiddleknife.Credentials;
import org.bonsaimind.minecraftmiddleknife.pre16.AuthenticatedSession;
import org.bonsaimind.minecraftmiddleknife.pre16.AuthenticationException;
import org.bonsaimind.minecraftmiddleknife.pre16.Authenticator;

public class Main {

	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static final String NAME = "EasyMineAuth";
	public static final String VERSION = "0.1";

	public static void main(String[] args) {
		LOGGER.setLevel(Level.ALL);

		boolean noNewLine = false;
		String password = "";
		boolean printFullResponse = false;
		String server = Authenticator.MOJANG_SERVER;
		String session = "";
		String username = "";
		String version = Authenticator.DEFAULT_LAUNCHER_VERSION;

		for (String arg : args) {
			if (arg.equals("--help")) {
				printHelp();
				System.exit(0);
			} else if (arg.equals("--no-new-line")) {
				noNewLine = true;
			} else if (arg.startsWith("--password=")) {
				password = arg.substring(11);
			} else if (arg.equals("--print-full-response")) {
				printFullResponse = true;
			} else if (arg.startsWith("--server=")) {
				server = arg.substring(9);
			} else if (arg.startsWith("--session=")) {
				session = arg.substring(10);
			} else if (arg.equals("--silent") || arg.equals("--shut-up")) {
				LOGGER.setLevel(Level.OFF);
			} else if (arg.startsWith("--username=")) {
				username = arg.substring(11);
			} else if (arg.equals("--version")) {
				printVersion();
				System.exit(0);
			} else if (arg.startsWith("--version=")) {
				version = arg.substring(10);
			} else {
				LOGGER.log(Level.SEVERE, "Unknown argument: {0}", arg);
				System.exit(1);
			}
		}

		if (session == null || session.length() == 0) {
			login(server, username, password, version, noNewLine, printFullResponse);
		} else {
			keepAlive(server, username, session);
		}
	}

	/**
	 * Performs a keep-alive request.
	 * @param server
	 * @param username
	 * @param session
	 */
	public static void keepAlive(String server, String username, String sessionId) {
		AuthenticatedSession session = new AuthenticatedSession(0, "", username, sessionId, "");
		try {
			Authenticator.keepAlive(new URL(server), session);
		} catch (MalformedURLException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		} catch (AuthenticationException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		}
	}

	/**
	 * Performs a login.
	 * @param server
	 * @param username
	 * @param password
	 * @param version
	 */
	public static void login(String server, String username, String password, String version, boolean noNewLine, boolean printFullResponse) {
		Credentials credentials = new Credentials(username, password);
		AuthenticatedSession session = null;
		try {
			session = Authenticator.authenticate(new URL(server), version, credentials);
		} catch (MalformedURLException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		} catch (AuthenticationException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		}

		if (printFullResponse) {
			System.out.print(session.toString());
		} else {
			System.out.print(session.getSessionId());
		}
		if (!noNewLine) {
			System.out.println();
		}
	}

	private static void printVersion() {
		System.out.println(NAME + " " + VERSION);
		System.out.println("Copyright 2013 Robert 'Bobby' Zenz. All rights reserved.");
		System.out.println("Licensed under 2-clause-BSD.");
	}

	private static void printHelp() {
		System.out.println("Usage: " + NAME + ".jar [OPTIONS]");
		System.out.println("Get a Minecraft session.");
		System.out.println("");

		InputStream stream = Main.class.getResourceAsStream("/org/bonsaimind/easymineauth/help.text");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "Failed to read the help-file!", ex);
		}
	}
}
