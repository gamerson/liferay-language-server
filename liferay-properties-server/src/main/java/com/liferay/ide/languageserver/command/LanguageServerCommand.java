/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		//String liferayLanguageServerPort = System.getProperty("liferayLanguageServerPort");

		int port = 55555;

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