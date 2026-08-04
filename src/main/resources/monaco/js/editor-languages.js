function registerLanguages() {
    monaco.languages.register({ id: 'glsl' });
    monaco.languages.setMonarchTokensProvider('glsl', {
        tokenizer: {
            root: [
                [/\b(attribute|const|uniform|varying|break|continue|do|for|while|if|else|in|out|inout|float|int|void|bool|true|false|lowp|mediump|highp|precision|invariant|discard|return|mat2|mat3|mat4|vec2|vec3|vec4|ivec2|ivec3|ivec4|bvec2|bvec3|bvec4|sampler2D|samplerCube|struct)\b/, 'keyword'],
                [/\b[a-zA-Z_][a-zA-Z0-9_]*(?=\s*\()/, 'function'],
                [/\b[a-zA-Z_][a-zA-Z0-9_]*\b/, 'identifier'],
                [/[{}()\[\]]/, '@brackets'],
                [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
                [/\b0[xX][0-9a-fA-F]+\b/, 'number.hex'],
                [/\b\d+\b/, 'number'],
                [/\/\*/, 'comment', '@comment'],
                [/\/\/.*$/, 'comment'],
                [/#\s*(define|undef|if|ifdef|ifndef|else|elif|endif|error|pragma|extension|version|line)\b/, 'keyword.directive']
            ],
            comment: [
                [/[^\/*]+/, 'comment'],
                [/\/\*/, 'comment', '@push'],
                ["\\*/", 'comment', '@pop'],
                [/[\/*]/, 'comment']
            ]
        }
    });

    monaco.languages.register({ id: 'csv' });
    monaco.languages.setMonarchTokensProvider('csv', {
        tokenizer: {
            root: [
                [/[^,\r\n"]+/, 'string'],
                [/,/, 'delimiter'],
                [/"/, 'string.quote', '@string']
            ],
            string: [
                [/[^"]+/, 'string'],
                [/""/, 'string.escape'],
                [/"/, 'string.quote', '@pop']
            ]
        }
    });

    monaco.languages.register({ id: 'mcfunction' });
    monaco.languages.setMonarchTokensProvider('mcfunction', {
        ignoreCase: false,
        keywords: ['achievement','actionbar','advancement','attribute','ban','ban-ip','banlist','bossbar','clear','clone','data','datapack','debug','defaultgamemode','deop','difficulty','effect','enchant','execute','experience','fill','function','gamemode','gamerule','give','help','item','kick','kill','list','locate','loot','op','pardon','pardon-ip','particle','playsound','publish','recipe','replaceitem','run','save-all','save-off','save-on','say','scoreboard','seed','setblock','setidletimeout','setworldspawn','spawnpoint','spreadplayers','stop','stopsound','summon','tag','team','teams','teleport','tellraw','time','title','tp','weather','whitelist','xp'],
        typeKeywords: ['add','advancements','anchored','aqua','arbitrary','as','at','black','block','blue','bold','byte','color','creative','dark_aqua','dark_blue','dark_gray','dark_green','dark_purple','dark_red','distance','double','dx','dy','dz','entity','extra','eyes','false','feet','float','furthest','gold','gray','green','if','int','italic','level','light_purple','limit','list','long','name','nbt','nearest','obfuscated','objectives','offset','players','positioned','random','red','remove','replace','reset','result','scores','set','short','sort','spectator','store','strikethrough','success','survival','text','true','type','underline','unless','values','white','x','x_rotation','y','y_rotation','yellow','z'],
        operators: ['=','+=','-=','*=','/=','%=','<','>','<=','>=','<>',',',':'],
        tokenizer: {
            root: [
                [/#[^\n]*/, 'comment'],
                [/@[a-eprs]\b/, 'variable.predefined'],
                [/[~^]-?(?:\d+(?:\.\d+)?)?/, 'keyword'],
                [/[a-zA-Z_][a-zA-Z0-9_-]*/, { cases: { '@keywords': 'keyword', '@typeKeywords': 'type', '@default': 'identifier' } }],
                { include: '@whitespace' },
                [/-?\d+\.\d+(?:[eE][\-+]?\d+)?/, 'number.float'],
                [/-?\d+/, 'number'],
                [/"([^"\\]|\\.)*$/, 'string.invalid'],
                [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],
                [/[=><!+\-*/%:]+/, { cases: { '@operators': 'operator', '@default': '' } }],
                [/,/, 'delimiter'],
                [/[{}()\[\]]/, '@brackets']
            ],
            whitespace: [ [/[ \t\r\n]+/, 'white'] ],
            string: [ [/[^\\"]+/, 'string'], [/\\./, 'string.escape'], [/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }] ]
        }
    });

    monaco.languages.register({ id: 'java' });
    monaco.languages.setMonarchTokensProvider('java', {
        defaultToken: '',
        tokenPostfix: '.java',
        keywords: [
            'abstract', 'assert', 'break', 'case', 'catch', 'class',
            'const', 'continue', 'default', 'do', 'else', 'enum', 'extends', 'final',
            'finally', 'for', 'goto', 'if', 'implements', 'import', 'instanceof',
            'interface', 'native', 'new', 'package', 'private', 'protected', 'public',
            'return', 'static', 'strictfp', 'switch', 'synchronized',
            'throw', 'throws', 'transient', 'try', 'volatile', 'while', 'yield', 'record',
            'sealed', 'non-sealed', 'var', 'permits'
        ],
        primitives: [
            'boolean', 'byte', 'char', 'double', 'float', 'int', 'long', 'short', 'void'
        ],
        operators: [
            '=', '>', '<', '!', '~', '?', ':', '==', '<=', '>=', '!=',
            '&&', '||', '++', '--', '+', '-', '*', '/', '&', '|', '^', '%',
            '<<', '>>', '>>>', '+=', '-=', '*=', '/=', '&=', '|=', '^=',
            '%=', '<<=', '>>=', '>>>='
        ],
        symbols: /[=><!\~?:&|+\-*\/\^%]+/,
        escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
        tokenizer: {
            root: [
                [/@[a-zA-Z_]\w*/, 'annotation'],
                [/\b(this|super)\b/, 'variable.language'],
                [/\b(true|false|null)\b/, 'constant.language'],
                [/\b[A-Z_][A-Z0-9_]{2,}\b/, 'constant'],
                [/\b[A-Z][a-zA-Z0-9_]*\b/, 'type.identifier'],
                [/\b[a-zA-Z_]\w*(?=\s*\()/, 'entity.name.function'],
                [/[a-zA-Z_$][\w$]*/, {
                    cases: {
                        '@keywords': 'keyword',
                        '@primitives': 'type.primitive',
                        '@default': 'variable'
                    }
                }],
                { include: '@whitespace' },
                [/[{}()\[\]]/, '@brackets'],
                [/[<>](?!@symbols)/, '@brackets'],
                [/@symbols/, {
                    cases: {
                        '@operators': 'operator',
                        '@default': ''
                    }
                }],
                [/\d*\.\d+([eE][\-+]?\d+)?[fFdD]?/, 'number.float'],
                [/0[xX][0-9a-fA-F]+[lL]?/, 'number.hex'],
                [/\d+[lL]?/, 'number'],
                [/[;,.]/, 'delimiter'],
                [/"([^"\\]|\\.)*"/, 'string'],
                [/'([^'\\]|\\.)*'/, 'string.char'],
            ],
            whitespace: [
                [/[ \t\r\n]+/, 'white'],
                [/\/\*/, 'comment', '@comment'],
                [/\/\/.*$/, 'comment'],
            ],
            comment: [
                [/[^\/*]+/, 'comment'],
                [/\/\*/, 'comment', '@push'],
                ["\\*/", 'comment', '@pop'],
                [/[\/*]/, 'comment']
            ],
        }
    });

    (function registerGroovy() {
        var id = 'groovy';
        monaco.languages.register({ id: id, aliases: ['Groovy'] });

        function getTokens(tokens, divider) {
            return tokens.split(divider || "|");
        }

        var brackets = [
            ["{", "}"],
            ["[", "]"],
            ["(", ")"]
        ];

        var autoClosingPairs = [
            { open: "{", close: "}" },
            { open: "[", close: "]" },
            { open: "(", close: ")" },
            { open: '"', close: '"' },
            { open: "'", close: "'" },
            { open: "`", close: "`" }
        ];

        monaco.languages.setMonarchTokensProvider(id, {
            tokenPostfix: ".groovy",
            keywords: getTokens("assert|with|abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|def|float|native|super|while|in|as"),
            typeKeywords: getTokens("Long|Integer|Short|Byte|Double|Number|Float|Character|Boolean|StackTraceElement|Appendable|StringBuffer|Iterable|ThreadGroup|Runnable|Thread|IllegalMonitorStateException|StackOverflowError|OutOfMemoryError|VirtualMachineError|ArrayStoreException|ClassCastException|LinkageError|NoClassDefFoundError|ClassNotFoundException|RuntimeException|Exception|ThreadDeath|Error|Throwable|System|ClassLoader|Cloneable|Class|CharSequence|Comparable|String|Object"),
            constants: getTokens("null|Infinity|NaN|undefined|true|false"),
            builtinFunctions: getTokens("AbstractMethodError|AssertionError|ClassCircularityError|ClassFormatError|Deprecated|EnumConstantNotPresentException|ExceptionInInitializerError|IllegalAccessError|IllegalThreadStateException|InstantiationError|InternalError|NegativeArraySizeException|NoSuchFieldError|Override|Process|ProcessBuilder|SecurityManager|StringIndexOutOfBoundsException|SuppressWarnings|TypeNotPresentException|UnknownError|UnsatisfiedLinkError|UnsupportedClassVersionError|VerifyError|InstantiationException|IndexOutOfBoundsException|ArrayIndexOutOfBoundsException|CloneNotSupportedException|NoSuchFieldException|IllegalArgumentException|NumberFormatException|SecurityException|Void|InheritableThreadLocal|IllegalStateException|InterruptedException|NoSuchMethodException|IllegalAccessException|UnsupportedOperationException|Enum|StrictMath|Package|Compiler|Readable|Runtime|StringBuilder|Math|IncompatibleClassChangeError|NoSuchMethodError|ThreadLocal|RuntimePermission|ArithmeticException|NullPointerException"),
            operators: [".", ".&", ".@", "?.", "*", "*.", "*:", "~", "!", "++", "--", "**", "+", "-", "*", "/", "%", "<<", ">>", ">>>", "..", "..<", "<", "<=", ">", ">=", "==", "!=", "<=>", "===", "!==", "=~", "==~", "^", "|", "&&", "||", "?", ":", "?:", "=", "**=", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=", ">>>=", "&=", "^=", "|=", "?="],
            symbols: /[=><!\~?:&|+\-*/^%]+/,
            escapes: /\\(?:[abfnrtv\\"'`]|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
            regexpctl: /[(){}[\]$^|\-*+?.]/.source,
            regexpesc: /\\(?:[bBdDfnrstvwWn0\\/]|@regexpctl|c[A-Z]|x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4})/,

            tokenizer: {
                root: [
                    { include: "@whitespace" },
                    [/\/(?=([^\\/]|\\.)+\/([dgimsuy]*)(\s*)(\.|\;|,|\)|\]|\}|$))/, { token: "regexp", bracket: "@open", next: "@regexp" }],
                    { include: "@comments" },
                    { include: "@numbers" },
                    { include: "common" },
                    [/[;,.]/, "delimiter"],
                    [/[(){}[\]]/, "@brackets"],
                    [/[a-zA-Z_$]\w*/, { cases: { "@keywords": "keyword", "@typeKeywords": "type", "@constants": "constant.groovy", "@builtinFunctions": "constant.other.color", "@default": "identifier" } }],
                    [/@symbols/, { cases: { "@operators": "operator", "@default": "" } }]
                ],
                common: [
                    [/[()[\]]/, "@brackets"],
                    [/[<>](?!@symbols)/, "@brackets"],
                    [/@symbols/, { cases: { "@operators": "delimiter", "@default": "" } }],
                    [/\/(?=([^\\/]|\\.)+\/([gimsuy]*)(\s*)(\.|\;|\/|,|\)|\]|\}|$))/, { token: "regexp", bracket: "@open", next: "@regexp" }],
                    [/[;,.]/, "delimiter"],
                    [/"([^"\\]|\\.)*$/, "string.invalid"],
                    [/'([^'\\]|\\.)*$/, "string.invalid"],
                    [/"/, "string", "@string_double"],
                    [/'/, "string", "@string_single"]
                ],
                whitespace: [[/\s+/, "white"]],
                comments: [
                    [/\/\/.*/, "comment"],
                    [/\/\*/, { token: "comment.quote", next: "@comment" }]
                ],
                comment: [
                    [/[^*/]+/, "comment"],
                    [/\*\//, { token: "comment.quote", next: "@pop" }],
                    [/./, "comment"]
                ],
                numbers: [
                    [/[+-]?\d+(?:(?:\.\d*)?(?:[eE][+-]?\d+)?)?f?\b/, "number.float"],
                    [/[+-]?(?:0[obx])?\d+(?:u?[lst]?)?\b/, "number"]
                ],
                regexp: [
                    [/(\{)(\d+(?:,\d*)?)(\})/, ["regexp.escape.control", "regexp.escape.control", "regexp.escape.control"]],
                    [/(\[)(\^?)(?=(?:[^\]\\\/]|\\.)+)/, ["regexp.escape.control", { token: "regexp.escape.control", next: "@regexrange" }]],
                    [/(\()(\?:|\?=|\?!)/, ["regexp.escape.control", "regexp.escape.control"]],
                    [/[()]/, "regexp.escape.control"],
                    [/@regexpctl/, "regexp.escape.control"],
                    [/[^\\\/]/, "regexp"],
                    [/@regexpesc/, "regexp.escape"],
                    [/\\\./, "regexp.invalid"],
                    [/(\/)([gimsuy]*)/, [{ token: "regexp", bracket: "@close", next: "@pop" }, "keyword.other"]]
                ],
                regexrange: [
                    [/-/, "regexp.escape.control"],
                    [/\^/, "regexp.invalid"],
                    [/@regexpesc/, "regexp.escape"],
                    [/[^\]]/, "regexp"],
                    [/\]/, { token: "regexp.escape.control", next: "@pop", bracket: "@close" }]
                ],
                string_double: [
                    [/\$\{/, { token: "delimiter.bracket", next: "@bracketCounting" }],
                    [/[^\\"$]+/, "string"],
                    [/[^\\"]+/, "string"],
                    [/@escapes/, "string.escape"],
                    [/\\./, "string.escape.invalid"],
                    [/"/, "string", "@pop"]
                ],
                string_single: [
                    [/[^\\']+/, "string"],
                    [/@escapes/, "string.escape"],
                    [/\\./, "string.escape.invalid"],
                    [/'/, "string", "@pop"]
                ],
                bracketCounting: [
                    [/\{/, "delimiter.bracket", "@bracketCounting"],
                    [/\}/, "delimiter.bracket", "@pop"],
                    { include: "common" }
                ]
            }
        });

        monaco.languages.setLanguageConfiguration(id, {
            comments: { lineComment: "//", blockComment: ["/*", "*/"] },
            brackets: brackets,
            autoClosingPairs: autoClosingPairs,
            surroundingPairs: autoClosingPairs,
            wordPattern: /(-?\d*\.\d\w*)|([^`~!@#%^&*()\-=+[{\]}\\\|;:'",.\/?\s]+)/g
        });
    })();
}