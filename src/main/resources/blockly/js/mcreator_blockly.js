var global_variables = [];

Blockly.HSV_SATURATION = 0.37;
Blockly.HSV_VALUE = 0.6;

var blockly = document.getElementById('blockly');
var workspace = Blockly.inject(blockly, {
    media: '@RESOURCES_PATH',
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

Blockly.ContextMenu.blockHelpOption = function () {
    return null;
};

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

function arrayToBlocklyDropDownArray(arrorig) {
    var retval = [];
    arrorig.forEach(function (element) {
        retval.push(["" + element, "" + element]);
    });
    return retval;
}

function jsonToBlocklyDropDownArray(json) {
    var map = JSON.parse(json);
    var retval = [];
    Object.keys(map).forEach(function (key) {
        retval.push(["" + map[key], "" + key]);
    });
    return retval;
}

function getBlocklyWorkspaceSVG() {
    var scaleFactor = 1;

    var cp = Blockly.mainWorkspace.svgBlockCanvas_.cloneNode(true);
    cp.removeAttribute("width");
    cp.removeAttribute("height");
    cp.removeAttribute("transform");

    var styleElem = document.createElementNS("http://www.w3.org/2000/svg", "style");
    styleElem.textContent = Blockly.Css.CONTENT.join('') + getallcss();
    cp.insertBefore(styleElem, cp.firstChild);

    var bbox = Blockly.mainWorkspace.svgBlockCanvas_.getBBox();
    var xml = new XMLSerializer().serializeToString(cp);
    xml = '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="' + bbox.width + '" height="' + bbox.height +
        '" viewBox="' + bbox.x + ' ' + bbox.y + ' ' + bbox.width + ' ' + bbox.height + '"><rect width="100%" height="100%" fill="white"></rect>' + xml + '</svg>';
    return xml;
}

function getallcss() {
    var css = "", styletags = document.getElementsByTagName("style");
    for (var i = 0; i < styletags.length; i++)
        css += styletags[i].innerHTML;
    return css;
}