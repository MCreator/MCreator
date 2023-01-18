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
    return function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf(listType))), fieldName);
    };
}

// Helper function to use in Blockly extensions that append a message and a dropdown
function appendDropDownWithMessage(messageKey, listType, fieldName) {
    return function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.extension." + messageKey))
            .appendField(new Blockly.FieldDropdown(
                arrayToBlocklyDropDownArray(javabridge.getListOf(listType))), fieldName);
    };
}

// Helper function to use in Blockly extensions that register one dropdown to update contents of another
function appendAutoReloadingDropDown(sourceName, sourceList, targetName, targetList) {
    return function () {
        const source = new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(javabridge.getListOf(sourceList)));
        this.appendDummyInput().appendField(source, sourceName);
        const targetData = function () {
            return javabridge.getListOfFrom(targetList, this.getValue());
        };
        const dummy = this.appendDummyInput();
        dummy.appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(targetData.call(source))), targetName);
        const sourceLoadState = source.loadState; // without this override field tries to parse field value
        source.loadState = function (state) { // using textToDom method which then fails as its argument is not xml text
            sourceLoadState.call(this, state);
        };
        const sourceFromXml = source.fromXml;
        source.fromXml = function (fieldElement) {
            sourceFromXml.call(this, fieldElement);
            dummy.removeField(targetName);
            dummy.appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(targetData.call(this))), targetName);
        };
        const sourceOnItemSelected = source.onItemSelected_;
        source.onItemSelected_ = function (menu, menuItem) {
            sourceOnItemSelected.call(this, menu, menuItem);
            dummy.removeField(targetName);
            dummy.appendField(new Blockly.FieldDropdown(arrayToBlocklyDropDownArray(targetData.call(this))), targetName);
        };
    };
}

// A function to properly convert workspace to XML (google/blockly#6738)
function workspaceToXML() {
    const treeXml = Blockly.Xml.workspaceToDom(workspace, true);

    // Remove variables child if present
    const variablesElements = treeXml.getElementsByTagName("variables");
    for (const varEl of variablesElements) {
        treeXml.removeChild(varEl);
    }

    // Add variables child on top of DOM
    const variablesElement = Blockly.Xml.variablesToDom(workspace.getAllVariables());
    if (variablesElement.hasChildNodes()) {
        treeXml.prepend(variablesElement);
    }

    return Blockly.Xml.domToText(treeXml);
}