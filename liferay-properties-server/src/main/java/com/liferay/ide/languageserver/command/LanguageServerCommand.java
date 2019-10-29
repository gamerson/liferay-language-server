/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.languageserver.command;

import com.liferay.ide.languageserver.LiferayLanguageServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * @author Terry Jia
 */
public class LanguageServerCommand {

	public static void main(String[] args) throws Exception {
		String liferayLanguageServerPort = System.getProperty(
			"liferayLanguageServerPort");

		int port = Integer.parseInt(liferayLanguageServerPort);

		String liferayLanguageServerSocketServer = System.getProperty(
			"liferayLanguageServerSocketServer");

		boolean socketServer = false;

		try {
			socketServer = Boolean.parseBoolean(
				liferayLanguageServerSocketServer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (socketServer) {
			ServerSocket serverSocket = new ServerSocket(port);

			Thread socketThread = new Thread(
				() -> {
					try {
						_socket = serverSocket.accept();
					}
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
					finally {
						try {
							serverSocket.close();
						}
						catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				});

			socketThread.start();

			try {
				socketThread.join();
			}
			catch (InterruptedException ie) {
				Thread thread = Thread.currentThread();

				thread.interrupt();

				return;
			}

			if (_socket == null) {
				return;
			}
		}
		else {
			_socket = new Socket("localhost", port);
		}

		InputStream in = _socket.getInputStream();
		OutputStream out = _socket.getOutputStream();

		LiferayLanguageServer server = new LiferayLanguageServer();

		Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(
			server, in, out);

		LanguageClient client = launcher.getRemoteProxy();

		server.setRemoteProxy(client);

		launcher.startListening();
	}

	private static Socket _socket;

}