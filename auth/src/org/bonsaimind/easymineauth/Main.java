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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bonsaimind.minecraftmiddleknife.Authentication;
import org.bonsaimind.minecraftmiddleknife.AuthenticationResponse;

public class Main {

	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static final String NAME = "EasyMineAuth";
	public static final String VERSION = "0.1";

	public static void main(String[] args) {
		LOGGER.setLevel(Level.ALL);

		boolean noNewLine = false;
		String password = "";
		String server = Authentication.MOJANG_SERVER;
		String session = "";
		String username = "";
		String version = Authentication.LAUNCHER_VERSION;

		for (String arg : args) {
			if (arg.equals("--help")) {
				printHelp();
				System.exit(0);
			} else if (arg.equals("--no-new-line")) {
				noNewLine = true;
			} else if (arg.startsWith("--password=")) {
				password = arg.substring(11);
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
			login(server, username, password, version, noNewLine);
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
	public static void keepAlive(String server, String username, String session) {
		Authentication authentication = new Authentication();
		authentication.setRealUsername(username);
		authentication.setSessionId(session);
		try {
			authentication.keepAlive();
		} catch (UnsupportedEncodingException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		} catch (MalformedURLException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		} catch (IOException ex) {
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
	public static void login(String server, String username, String password, String version, boolean noNewLine) {
		Authentication authentication = new Authentication(server, version, username, password);

		try {
			AuthenticationResponse response = authentication.authenticate();

			if (response == AuthenticationResponse.SUCCESS) {
				if (noNewLine) {
					System.out.print(authentication.getSessionId());
				} else {
					System.out.println(authentication.getSessionId());
				}
			} else {
				LOGGER.log(Level.SEVERE, response.getMessage());
				System.exit(1);
			}
		} catch (UnsupportedEncodingException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		} catch (MalformedURLException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
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