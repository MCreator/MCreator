let global_variables = [];

Blockly.HSV_SATURATION = 0.37;
Blockly.HSV_VALUE = 0.6;

const blockly = document.getElementById('blockly');
const workspace = Blockly.inject(blockly, {
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
    let retval = [];

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
    let retval = "";
    workspace.getVariableMap().getAllVariables().forEach(function (v, index, array) {
        retval += ((v.name + ";" + v.type) + (index < array.length - 1 ? ":" : ""));
    });
    return retval;
}

function arrayToBlocklyDropDownArray(arrorig) {
    let retval = [];
    arrorig.forEach(function (element) {
        retval.push(["" + element, "" + element]);
    });
    return retval;
}

function jsonToBlocklyDropDownArray(json) {
    let map = JSON.parse(json);
    let retval = [];
    Object.keys(map).forEach(function (key) {
        retval.push(["" + map[key], "" + key]);
    });
    return retval;
}

// Helper function to use in Blockly extensions that append a dropdown
function appendDropDown(listType, fieldName) {
    return function() {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf(listType))), fieldName);
    };
}

// Helper function to use in Blockly extensions that append a message and a dropdown
function appendDropDownWithMessage(messageKey, listType, fieldName) {
    return function() {
        this.appendDummyInput().appendField(javabridge.t("blockly.extension." + messageKey))
            .appendField(new Blockly.FieldDropdown(
                arrayToBlocklyDropDownArray(javabridge.getListOf(listType))), fieldName);
    };
}

// A function to properly convert workspace to XML (google/blockly#6738)
function workspaceToXML() {
    const treeXml = Blockly.utils.xml.createElement('xml');
    const variablesElement = Blockly.Xml.variablesToDom(Blockly.Variables.allUsedVarModels(workspace));
    if (variablesElement.hasChildNodes()) {
        treeXml.appendChild(variablesElement);
    }
    const comments = workspace.getTopComments(true);
    for (let i = 0; i < comments.length; i++) {
        const comment = comments[i];
        treeXml.appendChild(comment.toXmlWithXY(true));
    }
    const blocks = workspace.getTopBlocks(true);
    for (let i = 0; i < blocks.length; i++) {
        const block = blocks[i];
        treeXml.appendChild(Blockly.Xml.blockToDomWithXY(block, true));
    }
    return Blockly.Xml.domToText(treeXml);
}