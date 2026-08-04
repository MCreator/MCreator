var externalClasses = [];

window.setExternalClasses = function (classesList) {
    externalClasses = classesList || [];
};

var javaKeywords = [
    "abstract", "boolean", "break", "byte", "case", "catch", "char", "class",
    "continue", "default", "do", "double", "else", "enum", "extends", "final",
    "finally", "float", "for", "if", "implements", "import", "instanceof", "int",
    "interface", "long", "native", "new", "null", "package", "private", "protected",
    "public", "return", "short", "static", "super", "switch", "synchronized",
    "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
    "true", "false"
];

function registerJavaAutocomplete(editor) {
    monaco.languages.registerCompletionItemProvider('java', {
        triggerCharacters: ['.'],
        provideCompletionItems: function (model, position, context) {
            if (window.autocompleteEnabled === false) {
                return { suggestions: [] };
            }

            var lineContent = model.getLineContent(position.lineNumber);
            var lineUntilPosition = lineContent.substring(0, position.column - 1);

            if (window.autocompleteMode === 'Trigger on dot' && !lineUntilPosition.endsWith('.') && context.triggerKind !== monaco.languages.CompletionTriggerKind.Invoke) {
                return { suggestions: [] };
            }

            var wordInfo = model.getWordUntilPosition(position);
            var range = {
                startLineNumber: position.lineNumber,
                endLineNumber: position.lineNumber,
                startColumn: wordInfo.startColumn,
                endColumn: wordInfo.endColumn
            };

            var textBeforeCursor = lineUntilPosition.substring(0, lineUntilPosition.length - wordInfo.word.length);
            var isDotContext = textBeforeCursor.trim().endsWith('.');

            if (isDotContext) {
                return handleDotCompletion(model, position, range, textBeforeCursor);
            } else {
                return handleGeneralCompletion(model, position, range, lineUntilPosition, wordInfo);
            }
        }
    });
}

function handleDotCompletion(model, position, range, textBeforeCursor) {
    var beforeDot = textBeforeCursor.substring(0, textBeforeCursor.lastIndexOf('.')).trim();
    var targetName = "";
    var depth = 0;
    for (var i = beforeDot.length - 1; i >= 0; i--) {
        var char = beforeDot.charAt(i);
        if (char === ')') depth++;
        else if (char === '(') depth--;
        else if (depth === 0) {
            if (!char.match(/[a-zA-Z0-9_.]/)) {
                break;
            }
        }
        targetName = char + targetName;
    }
    targetName = targetName.trim();

    if (targetName && window.javabridge && window.javabridge.getDotCompletions) {
        return new Promise(function (resolve) {
            var fullTextBeforeCursor = model.getValueInRange({
                startLineNumber: 1,
                startColumn: 1,
                endLineNumber: position.lineNumber,
                endColumn: position.column
            });
            window.javabridge.getDotCompletions(targetName, model.getValue(), fullTextBeforeCursor, {
                callback: function (items) {
                    var dotSuggestions = [];
                    if (Array.isArray(items)) {
                        for (var j = 0; j < items.length; j++) {
                            var item = items[j];
                            dotSuggestions.push({
                                label: item.label,
                                kind: item.kind === 'field' ? monaco.languages.CompletionItemKind.Field : monaco.languages.CompletionItemKind.Method,
                                detail: item.detail,
                                insertText: item.insertText,
                                insertTextRules: item.isSnippet ? monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet : undefined,
                                range: range
                            });
                        }
                    }
                    resolve({ suggestions: dotSuggestions });
                }
            });
        });
    }

    return { suggestions: [] };
}

function handleGeneralCompletion(model, position, range, lineUntilPosition, wordInfo) {
    var suggestions = [];

    // Add Java keywords
    for (var k = 0; k < javaKeywords.length; k++) {
        var kw = javaKeywords[k];
        suggestions.push({
            label: kw,
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: kw,
            range: range
        });
    }

    // Add words from the document
    var codeText = model.getValue();
    var docWordRegex = /\b([a-zA-Z_][a-zA-Z0-9_]*)\b/g;
    var match;
    var addedWords = {};

    for (var k = 0; k < javaKeywords.length; k++) {
        addedWords[javaKeywords[k]] = true;
    }

    while ((match = docWordRegex.exec(codeText)) !== null) {
        var w = match[1];
        if (w.length > 1 && !addedWords[w]) {
            addedWords[w] = true;
            suggestions.push({
                label: w,
                kind: monaco.languages.CompletionItemKind.Variable,
                insertText: w,
                range: range
            });
        }
    }

    // Add external classes when in a class-name context
    var textBeforeWord = lineUntilPosition.substring(0, lineUntilPosition.length - wordInfo.word.length).trim();
    var isClassContext = /(?:new|extends|implements|import|class|interface|enum)\s*$/.test(textBeforeWord) ||
                         textBeforeWord.endsWith('@') ||
                         (wordInfo.word.length > 0 && wordInfo.word[0] === wordInfo.word[0].toUpperCase() && wordInfo.word[0] !== wordInfo.word[0].toLowerCase());

    if (isClassContext) {
        var addedClasses = {};
        for (var i = 0; i < externalClasses.length; i++) {
            var cls = externalClasses[i];
            if (cls.name && !cls.name.includes('.') && !addedClasses[cls.name]) {
                addedClasses[cls.name] = true;
                suggestions.push({
                    label: cls.name,
                    kind: monaco.languages.CompletionItemKind.Class,
                    detail: cls.pkg,
                    documentation: cls.pkg ? (cls.pkg + '.' + cls.name) : cls.name,
                    insertText: cls.name,
                    range: range
                });
            }
        }
    }

    return { suggestions: suggestions };
}
