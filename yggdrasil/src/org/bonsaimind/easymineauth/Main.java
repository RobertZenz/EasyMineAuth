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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.Agent;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.AuthenticationRequest;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.AuthenticationResponse;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.RefreshRequest;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.RefreshResponse;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.ValidationRequest;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.Yggdrasil;
import org.bonsaimind.minecraftmiddleknife.post16.yggdrasil.YggdrasilError;

public class Main {

	public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static final String NAME = "EasyMineAuth - Yggdrasil";
	public static final String VERSION = "0.1";

	public static void main(String[] args) {
		LOGGER.setLevel(Level.ALL);

		String accessToken = "";
		String clientToken = "";
		boolean noNewLine = false;
		String password = "";
		String separator = ":";
		String server = "";
		String username = "";

		for (String arg : args) {
			if (arg.startsWith("--access-token=")) {
				accessToken = arg.substring(15);
			} else if (arg.startsWith("--client-token=")) {
				clientToken = arg.substring(15);
			} else if (arg.equals("--help")) {
				printHelp();
				System.exit(0);
			} else if (arg.equals("--no-new-line")) {
				noNewLine = true;
			} else if (arg.startsWith("--password=")) {
				password = arg.substring(11);
			} else if (arg.startsWith("--separator=")) {
				separator = arg.substring(12);
			} else if (arg.startsWith("--server=")) {
				server = arg.substring(9);
			} else if (arg.equals("--silent") || arg.equals("--shut-up")) {
				LOGGER.setLevel(Level.OFF);
			} else if (arg.startsWith("--username=")) {
				username = arg.substring(11);
			} else if (arg.equals("--version")) {
				printVersion();
				System.exit(0);
			} else {
				LOGGER.log(Level.SEVERE, "Unknown argument: {0}", arg);
				System.exit(1);
			}
		}

		if (accessToken.length() > 0 && clientToken.length() > 0) {
			refresh(server, accessToken, clientToken, separator, noNewLine);
		} else if (accessToken.length() > 0) {
			validate(server, accessToken);
		} else {
			authenticate(server, username, password, clientToken, separator, noNewLine);
		}
	}

	private static void authenticate(String server, String username, String password, String clientToken, String separator, boolean noNewLine) {
		AuthenticationRequest request = new AuthenticationRequest(Agent.MINECRAFT, username, password, clientToken);

		try {
			AuthenticationResponse response = Yggdrasil.authenticate(request);

			System.out.print(response.getAccessToken() + separator + response.getClientToken());

			if (!noNewLine) {
				System.out.println();
			}
		} catch (YggdrasilError ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		}
	}

	private static void refresh(String server, String accessToken, String clientToken, String separator, boolean noNewLine) {
		RefreshRequest request = new RefreshRequest(accessToken, clientToken);

		try {
			RefreshResponse response = Yggdrasil.refresh(request);

			System.out.print(response.getAccessToken() + separator + response.getClientToken());

			if (!noNewLine) {
				System.out.println();
			}
		} catch (YggdrasilError ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			System.exit(1);
		}
	}

	private static void validate(String server, String accessToken) {
		ValidationRequest request = new ValidationRequest(accessToken);

		try {
			Yggdrasil.validate(request);
		} catch (YggdrasilError ex) {
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
