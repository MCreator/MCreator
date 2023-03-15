let global_variables = [];

Blockly.HSV_SATURATION = 0.37;
Blockly.HSV_VALUE = 0.6;

const blockly = document.getElementById('blockly');
const workspace = Blockly.inject(blockly, {
    media: 'res/',
    oneBasedIndex: false,
    sounds: false,
    comments: MCR_BLOCKLY_PREF['comments'],
    collapse: MCR_BLOCKLY_PREF['collapse'],
    disable: false,
    trashcan: MCR_BLOCKLY_PREF['trashcan'],
    renderer: MCR_BLOCKLY_PREF['renderer'],
    zoom: {
        controls: false,
        wheel: true,
        startScale: 0.95,
        maxScale: MCR_BLOCKLY_PREF['maxScale'],
        minScale: MCR_BLOCKLY_PREF['minScale'],
        scaleSpeed: MCR_BLOCKLY_PREF['scaleSpeed']
    },
    toolbox: '<xml id="toolbox"><category name="" colour=""></category></xml>'
});

function blocklyEventFunction() {
    if (typeof javabridge !== "undefined")
        javabridge.triggerEvent();
}

workspace.addChangeListener(blocklyEventFunction);

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

// Helper function to use in Blockly extensions that check if output types of some children blocks are the same
// If sourceInput name is provided, types of inputs are compared to input with that name
// Otherwise, if the block is connected to a value input, types of inputs are compared to types accepted by that input
function validateInputTypes(inputNames, sourceInput, repeatingInputs=false) {
    return {
        prevTargetConnection_: null,

        onchange: function (changeEvent) {
            const targetConnection = sourceInput ?
                this.getInput(sourceInput) && this.getInput(sourceInput).connection :
                this.outputConnection && this.outputConnection.targetConnection;
            if (targetConnection && targetConnection.getCheck()) {
                for (let i = 0; i < inputNames.length; i++) {
                    if (repeatingInputs) {
                        for (let j = 0; this.getInput(inputNames[i] + j); j++)
                            this.checkInput_(changeEvent, inputNames[i] + j, targetConnection);
                    } else {
                        this.checkInput_(changeEvent, inputNames[i], targetConnection);
                    }
                }
            }
            this.prevTargetConnection_ = targetConnection;
        },

        checkInput_: function (changeEvent, inputName, targetConnection) {
            const connection = this.getInput(inputName).connection.targetConnection;
            const block = connection && connection.getSourceBlock();
            if (block && !block.workspace.connectionChecker.doTypeChecks(block.outputConnection, targetConnection)) {
                Blockly.Events.setGroup(changeEvent.group);
                if (targetConnection === this.prevTargetConnection_) {
                    this.unplug();
                    parentConnection.getSourceBlock().bumpNeighbours();
                } else {
                    block.unplug();
                    block.bumpNeighbours();
                }
                Blockly.Events.setGroup(false);
            }
        }
    };
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

// Helper function to use in Blockly extensions that validate repeating fields' values meant to be unique
// The nullValue function is used when mutator needs to set a valid value in the field right after its creation
function uniqueValueValidator(fieldName, nullValue) {
    return function (newValue) {
        for (let i = 0; this.sourceBlock_.getField(fieldName + i); i++) {
            if (this.sourceBlock_.getFieldValue(fieldName + i) == newValue && (fieldName + i) != this.name)
                return this.mutationInProcess_ ? nullValue() : null;
        }
        return newValue;
    };
}

// Helper function to find first index not taken by repeating fields with a certain name
function firstFreeIndex(block, fieldName, index) {
    const values = [];
    for (let i = 0; block.getField(fieldName + i); i++) {
        if (index && i == index)
            continue;
        values.push("" + block.getFieldValue(fieldName + i));
    }
    let retVal = 0;
    while (true) {
        if (values.indexOf("" + retVal) === -1)
            break;
        retVal++;
    }
    return retVal;
}

// Helper function to disable validators on newly created repeating fields when loading from save file
function validOnLoad(field) {
    const fieldFromXml = field.fromXml;
    field.fromXml = function (fieldElement) {
        const validator = this.validator_;
        this.validator_ = null;
        fieldFromXml.call(this, fieldElement);
        this.validator_ = validator;
    };
    const fieldLoadState = field.loadState; // we need to "override" this one too
    field.loadState = function (state) { // to not get caught by loadLegacyState function on this field
        const validator = this.validator_;
        this.validator_ = null;
        fieldLoadState.call(this, state);
        this.validator_ = validator;
    };
    return field;
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