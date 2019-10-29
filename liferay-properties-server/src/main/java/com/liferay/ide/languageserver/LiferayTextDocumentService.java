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

import com.liferay.ide.languageserver.completions.PropertiesCompletion;
import com.liferay.ide.languageserver.diagnostic.PropertiesDiagnostic;

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

/**
 * @author Terry Jia
 */
public class LiferayTextDocumentService implements TextDocumentService {

	public LiferayTextDocumentService(
		LiferayLanguageServer liferayLanguageServer) {

		_liferayLanguageServer = liferayLanguageServer;
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>>
		completion(CompletionParams completionParams) {

		PropertiesCompletion propertiesCompletion = new PropertiesCompletion(
			completionParams);

		List<CompletionItem> completionItems =
			propertiesCompletion.getCompletions(_currentContent);

		return CompletableFuture.supplyAsync(
			() -> Either.forLeft(completionItems));
	}

	@Override
	public void didChange(
		DidChangeTextDocumentParams didChangeTextDocumentParams) {

		List<TextDocumentContentChangeEvent> contentChangeEvents =
			didChangeTextDocumentParams.getContentChanges();

		TextDocumentContentChangeEvent contentChangeEvnet =
			contentChangeEvents.get(0);

		String currentContent = contentChangeEvnet.getText();

		_currentContent = currentContent;
	}

	@Override
	public void didClose(
		DidCloseTextDocumentParams didCloseTextDocumentParams) {
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
		TextDocumentItem textDocument =
			didOpenTextDocumentParams.getTextDocument();

		try {
			URI uri = new URI(textDocument.getUri());

			File file = new File(uri);

			PropertiesDiagnostic propertiesDiagnostic =
				new PropertiesDiagnostic(file);

			List<Diagnostic> diagnostics = propertiesDiagnostic.validate();

			LanguageClient client = _liferayLanguageServer.getLanguageClient();

			client.publishDiagnostics(
				new PublishDiagnosticsParams(
					textDocument.getUri(), diagnostics));
		}
		catch (URISyntaxException urise) {
		}
	}

	@Override
	public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
		TextDocumentIdentifier textDocument =
			didSaveTextDocumentParams.getTextDocument();

		try {
			URI uri = new URI(textDocument.getUri());

			File file = new File(uri);

			PropertiesDiagnostic propertiesDiagnostic =
				new PropertiesDiagnostic(file);

			List<Diagnostic> diagnostics = propertiesDiagnostic.validate();

			LanguageClient client = _liferayLanguageServer.getLanguageClient();

			client.publishDiagnostics(
				new PublishDiagnosticsParams(
					textDocument.getUri(), diagnostics));
		}
		catch (URISyntaxException urise) {
		}
	}

	private String _currentContent;
	private LiferayLanguageServer _liferayLanguageServer;

}