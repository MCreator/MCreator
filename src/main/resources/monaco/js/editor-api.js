function showNotification(message) {
    if (!editor) return;
    var node = document.createElement('div');
    node.className = 'monaco-toast-notification';
    node.innerText = message;
    node.style.cssText = 'position: absolute; top: 12px; right: 24px; background: rgba(30,30,30,0.9); color: #4ec9b0; padding: 6px 14px; border-radius: 4px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 12px; font-weight: 600; box-shadow: 0 4px 12px rgba(0,0,0,0.3); z-index: 9999; transition: opacity 0.3s ease; opacity: 0; pointer-events: none; border: 1px solid rgba(255,255,255,0.15);';
    document.body.appendChild(node);
    setTimeout(function() { node.style.opacity = '1'; }, 10);
    setTimeout(function() {
        node.style.opacity = '0';
        setTimeout(function() { if (node.parentNode) node.parentNode.removeChild(node); }, 300);
    }, 2000);
}

function initEditor(code, language, readOnly, fontSize, autocompleteEnabled, autocompleteMode, autocompleteDocWindow) {
    window.autocompleteEnabled = (autocompleteEnabled !== false);
    window.autocompleteMode = autocompleteMode || 'Smart';
    window.autocompleteDocWindow = (autocompleteDocWindow !== false);

    var quickSuggestionsVal = false;
    var triggerOnDotVal = false;

    if (window.autocompleteEnabled) {
        if (window.autocompleteMode === 'Smart') {
            quickSuggestionsVal = true;
            triggerOnDotVal = true;
        } else if (window.autocompleteMode === 'Trigger on dot') {
            quickSuggestionsVal = false;
            triggerOnDotVal = true;
        } else if (window.autocompleteMode === 'Manual') {
            quickSuggestionsVal = false;
            triggerOnDotVal = false;
        }
    }

    if (editor) {
        isSettingValue = true;
        editor.setValue(code);
        isSettingValue = false;
        var model = editor.getModel();
        if (model) {
            monaco.editor.setModelLanguage(model, language);
        }

        editor.updateOptions({
            readOnly: readOnly,
            fontSize: fontSize,
            glyphMargin: language === 'java',
            quickSuggestions: quickSuggestionsVal ? { other: true, comments: false, strings: false } : false,
            suggestOnTriggerCharacters: triggerOnDotVal,
            parameterHints: { enabled: window.autocompleteDocWindow },
            suggest: {
                showDocs: window.autocompleteDocWindow,
                showIcons: true
            },
            wordBasedSuggestions: false,
            mouseWheelZoom: true
        });
    } else {
        pendingInit = {
            code: code,
            language: language,
            readOnly: readOnly,
            fontSize: fontSize,
            autocompleteEnabled: autocompleteEnabled,
            autocompleteMode: autocompleteMode,
            autocompleteDocWindow: autocompleteDocWindow
        };
    }
}

function setEditorValue(val) {
    if (!editor) return;
    isSettingValue = true;
    editor.setValue(val);
    isSettingValue = false;
}

function setLanguage(lang) {
    if (!editor) return;
    var model = editor.getModel();
    if (model) {
        monaco.editor.setModelLanguage(model, lang);
    }
}

function setTheme(theme) {
    if (!editor) return;
    monaco.editor.setTheme(theme);
}

function setReadOnly(readOnly) {
    if (!editor) return;
    editor.updateOptions({ readOnly: readOnly });
}

function setFontSize(size) {
    if (!editor) return;
    editor.updateOptions({ fontSize: size });
}

function jumpToLine(lineNumber) {
    if (!editor) return;
    editor.revealLineInCenter(lineNumber);
    editor.setPosition({ lineNumber: lineNumber, column: 1 });
    editor.focus();
}

function triggerFind() {
    if (!editor) return;
    editor.getAction('actions.find').run();
}

function triggerReplace() {
    if (!editor) return;
    editor.getAction('editor.action.startFindReplaceAction').run();
}

function setCaretPosition(offset) {
    if (!editor) return;
    var model = editor.getModel();
    if (model) {
        var pos = model.getPositionAt(offset);
        editor.setPosition(pos);
        editor.revealPositionInCenter(pos);
        editor.focus();
    }
}

function formatCode() {
    if (!editor) return;
    editor.getAction('editor.action.formatDocument').run();
}

function resetPanel() {
    if (!editor) return;
    setEditorValue('');
    editor.getAction('editor.action.fontZoomReset').run();
    editor.setScrollTop(0);
    editor.setPosition({ lineNumber: 1, column: 1 });
    editor.focus();
}