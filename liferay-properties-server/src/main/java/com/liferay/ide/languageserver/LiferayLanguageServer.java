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

package com.liferay.ide.languageserver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.SaveOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextDocumentSyncOptions;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 * @author Terry Jia
 */
public class LiferayLanguageServer implements LanguageServer {

	public LiferayLanguageServer() {
		_textDocumentService = new LiferayTextDocumentService(this);
		_workspaceService = new LiferayWorkspaceService();
	}

	public void exit() {
	}

	public LanguageClient getLanguageClient() {
		return _languageClient;
	}

	public TextDocumentService getTextDocumentService() {
		return _textDocumentService;
	}

	public WorkspaceService getWorkspaceService() {
		return _workspaceService;
	}

	public CompletableFuture<InitializeResult> initialize(
		InitializeParams params) {

		ServerCapabilities serverCapabilities = new ServerCapabilities();

		TextDocumentSyncOptions textDocumentSyncOptions =
			new TextDocumentSyncOptions();

		textDocumentSyncOptions.setChange(TextDocumentSyncKind.Full);
		textDocumentSyncOptions.setOpenClose(false);
		textDocumentSyncOptions.setSave(new SaveOptions(true));
		textDocumentSyncOptions.setWillSave(false);
		textDocumentSyncOptions.setWillSaveWaitUntil(false);

		serverCapabilities.setTextDocumentSync(textDocumentSyncOptions);

		InitializeResult initializeResult = new InitializeResult(
			serverCapabilities);

		ServerCapabilities capabilities = initializeResult.getCapabilities();

		CompletionOptions completionOptions = new CompletionOptions();

		List<String> triggerCharacters = Arrays.asList("=", ",", ":");

		completionOptions.setTriggerCharacters(triggerCharacters);

		capabilities.setCompletionProvider(completionOptions);

		return CompletableFuture.supplyAsync(() -> initializeResult);
	}

	public void setRemoteProxy(LanguageClient languageClient) {
		_languageClient = languageClient;
	}

	public CompletableFuture<Object> shutdown() {
		return CompletableFuture.supplyAsync(() -> Boolean.TRUE);
	}

	private LanguageClient _languageClient;
	private TextDocumentService _textDocumentService;
	private WorkspaceService _workspaceService;

}