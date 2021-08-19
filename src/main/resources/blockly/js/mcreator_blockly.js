var global_variables = [];

Blockly.HSV_SATURATION = 0.37;
Blockly.HSV_VALUE = 0.6;

var blockly = document.getElementById('blockly');
var workspace = Blockly.inject(blockly, {
    media: 'res/',
    oneBasedIndex: false,
    sounds: false,
    comments: MCR_BLCKLY_PREF['comments'],
    collapse: MCR_BLCKLY_PREF['collapse'],
    disable: false,
    trashcan: MCR_BLCKLY_PREF['trashcan'],
    renderer: MCR_BLCKLY_PREF['renderer'],
    zoom: {
        controls: false,
        wheel: true,
        startScale: 0.95,
        maxScale: MCR_BLCKLY_PREF['maxScale'],
        minScale: MCR_BLCKLY_PREF['minScale'],
        scaleSpeed: MCR_BLCKLY_PREF['scaleSpeed']
    },
    toolbox: '<xml id="toolbox"><category name="" colour=""></category></xml>'
});

function blocklyEventFuntion() {
    if (typeof javabridge !== "undefined")
        javabridge.triggerEvent();
}

workspace.addChangeListener(blocklyEventFuntion);

window.addEventListener('resize', function () {
    Blockly.svgResize(workspace);
});
Blockly.svgResize(workspace);

// disable help entry
Blockly.Block.prototype.setHelpUrl = function () {
    return '';
}

// modify blockly to export all variables, not only used ones
Blockly.Variables.allUsedVarModels = function () {
    return workspace.getVariableMap().getAllVariables();
};

function getVariablesOfType(type) {
    var retval = [];

    workspace.getVariableMap().getAllVariables().forEach(function (v) {
        if (v.type === type)
            retval.push(["Local: " + v.name, "local:" + v.name]);
    });

    global_variables.forEach(function (v) {
        if (v.type === type)
            retval.push(["Global: " + v.name, "global:" + v.name]);
    });

    if (retval.length > 0)
        return retval;
    else
        return [["", ""]];
}

function getSerializedLocalVariables() {
    var retval = "";
    workspace.getVariableMap().getAllVariables().forEach(function (v, index, array) {
        retval += ((v.name + ";" + v.type) + (index < array.length - 1 ? ":" : ""));
    });
    return retval;
}

function arrayToBlocklyDropDownArray(arrorig, sorted) {
    var retval = [];
    arrorig.forEach(function (element) {
        retval.push(["" + element, "" + element]);
    });
    return sorted ? retval.sort(compareElements) : retval;
}

function arrayToBlocklyDropDownArrayWithReadableNames(arrorig, readablenames, sorted) {
    var retval = [];
    var length = arrorig.length;
    var nameslength = readablenames.length;
    for (var i = 0; i < length; i++) {
        retval.push(["" + (i < nameslength ? readablenames[i] : arrorig[i]), "" + arrorig[i]]);
    }
    return sorted ? retval.sort(compareElements) : retval;
}

function jsonToBlocklyDropDownArray(json) {
    var map = JSON.parse(json);
    var retval = [];
    Object.keys(map).forEach(function (key) {
        retval.push(["" + map[key], "" + key]);
    });
    return retval;
}

function compareElements(first, second) {
    // We compare the readable names of the elements
    var a = first[0];
    var b = second[0];
    // Custom values should be at the top of the list
    if (a.startsWith('CUSTOM:') && !b.startsWith('CUSTOM:'))
        return -1;
    else if (!a.startsWith('CUSTOM:') && b.startsWith('CUSTOM:'))
        return 1;
    else
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
}