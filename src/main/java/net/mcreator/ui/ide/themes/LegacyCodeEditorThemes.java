package net.mcreator.ui.ide.themes;

import java.util.HashMap;
import java.util.Map;

public class LegacyCodeEditorThemes {
	
	private static final Map<String, String> THEMES = new HashMap<>();

	static {
		// Default
		THEMES.put("Default", """
		{
			"base": "vs",
			"inherit": true,
			"colors": {
				"editor.background": "#ffffff",
				"editor.foreground": "#333333",
				"editor.lineHighlightBackground": "#f5f5f5",
				"editorCursor.foreground": "#000000",
				"editorBracketMatch.background": "#e8e8e8",
				"editorBracketMatch.border": "#b9b9b9",
				"editorLineNumber.foreground": "#999999"
			},
			"rules": [
				{ "token": "keyword", "foreground": "#0000ff", "fontStyle": "bold" },
				{ "token": "type", "foreground": "#267f99" },
				{ "token": "string", "foreground": "#a31515" },
				{ "token": "number", "foreground": "#098658" },
				{ "token": "comment", "foreground": "#008000", "fontStyle": "italic" },
				{ "token": "comment.doc", "foreground": "#3f5fbf", "fontStyle": "italic" },
				{ "token": "annotation", "foreground": "#008080" },
				{ "token": "function", "foreground": "#795E26" },
				{ "token": "entity.name.function", "foreground": "#795E26" }
			]
		}
		""");

		// Default-Alt
		THEMES.put("Default-Alt", """
		{
			"base": "vs",
			"inherit": true,
			"colors": {
				"editor.background": "#fdf6e3",
				"editor.foreground": "#657b83",
				"editor.lineHighlightBackground": "#eee8d5",
				"editorCursor.foreground": "#586e75",
				"editorBracketMatch.background": "#d3c6aa",
				"editorBracketMatch.border": "#b58900",
				"editorLineNumber.foreground": "#93a1a1"
			},
			"rules": [
				{ "token": "keyword", "foreground": "#859900", "fontStyle": "bold" },
				{ "token": "type", "foreground": "#b58900" },
				{ "token": "string", "foreground": "#2aa198" },
				{ "token": "number", "foreground": "#d33682" },
				{ "token": "comment", "foreground": "#93a1a1", "fontStyle": "italic" },
				{ "token": "comment.doc", "foreground": "#93a1a1", "fontStyle": "italic" },
				{ "token": "annotation", "foreground": "#cb4b16" },
				{ "token": "function", "foreground": "#268bd2" },
				{ "token": "entity.name.function", "foreground": "#268bd2" }
			]
		}
		""");

		// Dark
		THEMES.put("Dark", """
		{
			"base": "vs-dark",
			"inherit": true,
			"colors": {
				"editor.background": "#282c34",
				"editor.foreground": "#abb2bf",
				"editor.lineHighlightBackground": "#2c313c",
				"editorCursor.foreground": "#528bff",
				"editorBracketMatch.background": "#3b4048",
				"editorBracketMatch.border": "#528bff",
				"editorLineNumber.foreground": "#636d83"
			},
			"rules": [
				{ "token": "keyword", "foreground": "#c678dd", "fontStyle": "bold" },
				{ "token": "type", "foreground": "#e5c07b" },
				{ "token": "string", "foreground": "#98c379" },
				{ "token": "number", "foreground": "#d19a66" },
				{ "token": "comment", "foreground": "#7f848e", "fontStyle": "italic" },
				{ "token": "comment.doc", "foreground": "#7f848e", "fontStyle": "italic" },
				{ "token": "annotation", "foreground": "#e06c75" },
				{ "token": "function", "foreground": "#61afef" },
				{ "token": "entity.name.function", "foreground": "#61afef" }
			]
		}
		""");

		// Eclipse
		THEMES.put("Eclipse", """
		{
			"base": "vs",
			"inherit": true,
			"colors": {
				"editor.background": "#ffffff",
				"editor.foreground": "#000000",
				"editor.lineHighlightBackground": "#e8f2fe",
				"editorCursor.foreground": "#000000",
				"editorBracketMatch.background": "#d4d4d4",
				"editorBracketMatch.border": "#b9b9b9",
				"editorLineNumber.foreground": "#787878"
			},
			"rules": [
				{ "token": "keyword", "foreground": "#7f0055", "fontStyle": "bold" },
				{ "token": "type", "foreground": "#7f0055", "fontStyle": "bold" },
				{ "token": "string", "foreground": "#2a00ff" },
				{ "token": "number", "foreground": "#000000" },
				{ "token": "comment", "foreground": "#3f7f5f", "fontStyle": "italic" },
				{ "token": "comment.doc", "foreground": "#3f5fbf", "fontStyle": "italic" },
				{ "token": "annotation", "foreground": "#646464" },
				{ "token": "function", "foreground": "#604e03" },
				{ "token": "entity.name.function", "foreground": "#604e03" }
			]
		}
		""");

		// Idea
		THEMES.put("Idea", """
		{
			"base": "vs",
			"inherit": true,
			"colors": {
				"editor.background": "#ffffff",
				"editor.foreground": "#000000",
				"editor.lineHighlightBackground": "#f5f5f5",
				"editorCursor.foreground": "#000000",
				"editorBracketMatch.background": "#99ccff",
				"editorBracketMatch.border": "#99ccff",
				"editorLineNumber.foreground": "#999999"
			},
			"rules": [
				{ "token": "keyword", "foreground": "#000080", "fontStyle": "bold" },
				{ "token": "type", "foreground": "#000080", "fontStyle": "bold" },
				{ "token": "string", "foreground": "#008000", "fontStyle": "bold" },
				{ "token": "number", "foreground": "#0000ff" },
				{ "token": "comment", "foreground": "#808080", "fontStyle": "italic" },
				{ "token": "comment.doc", "foreground": "#808080", "fontStyle": "italic" },
				{ "token": "annotation", "foreground": "#008080" },
				{ "token": "function", "foreground": "#795E26" },
				{ "token": "entity.name.function", "foreground": "#795E26" }
			]
		}
		""");

		// Monokai
		THEMES.put("Monokai", """
		{
			"base": "vs-dark",
			"inherit": true,
			"colors": {
				"editor.background": "#272822",
				"editor.foreground": "#f8f8f2",
				"editor.lineHighlightBackground": "#3e3d32",
				"editorCursor.foreground": "#f8f8f0",
				"editorBracketMatch.background": "#49483e",
				"editorBracketMatch.border": "#49483e",
				"editorLineNumber.foreground": "#90908a"
			},
			"rules": [
				{ "token": "keyword", "foreground": "#f92672" },
				{ "token": "type", "foreground": "#66d9ef", "fontStyle": "italic" },
				{ "token": "string", "foreground": "#e6db74" },
				{ "token": "number", "foreground": "#ae81ff" },
				{ "token": "comment", "foreground": "#75715e" },
				{ "token": "comment.doc", "foreground": "#75715e" },
				{ "token": "annotation", "foreground": "#fd971f" },
				{ "token": "function", "foreground": "#a6e22e" },
				{ "token": "entity.name.function", "foreground": "#a6e22e" }
			]
		}
		""");

		// VS
		THEMES.put("VS", """
		{
			"base": "vs-dark",
			"inherit": true,
			"colors": {},
			"rules": [
				{ "token": "function", "foreground": "#DCDCAA" },
				{ "token": "entity.name.function", "foreground": "#DCDCAA" }
			]
		}
		""");
	}

	public static String getThemeJson(String themeName) {
		return THEMES.get(themeName);
	}
}