const PROCEDURE_PARAMETERS_MUTATOR_MIXIN = {
    paramsCount_: 0,

    mutationToDom: function() {
        const container = Blockly.utils.xml.createElement('mutation');
        container.setAttribute('params', this.paramsCount_);
        return container;
    },

    domToMutation: function(xmlElement) {
        this.paramsCount_ = parseInt(xmlElement.getAttribute('params'), 10);
        this.updateShape_();
    },

    saveExtraState: function() {
        return {
            'paramsCount': this.paramsCount_
        };
    },

    loadExtraState: function(state) {
        this.paramsCount_ = state['paramsCount'];
        this.updateShape_();
    },

    decompose: function(workspace) {
        const containerBlock = workspace.newBlock('procedure_call');
        containerBlock.initSvg();
        let connection = containerBlock.getInput('parameters').connection;
        for (let i = 1; i <= this.paramsCount_; i++) {
            const paramBlock = workspace.newBlock('procedure_parameter');
            paramBlock.initSvg();
            connection.connect(paramBlock.previousConnection);
            connection = paramBlock.nextConnection;
        }
        return containerBlock;
    },

    compose: function(containerBlock) {
        let paramBlock = containerBlock.getInputTargetBlock('parameters');
        const connections = [];
        while (paramBlock) {
            if (!paramBlock.isInsertionMarker())
                connections.push(paramBlock.valueConnection_);
            paramBlock = paramBlock.getNextBlock();
        }
        const names = {};
        for (let i = 0; i < this.paramsCount_; i++) {
            const connection = this.getInput('arg' + i).connection.targetConnection;
            if (connection && connections.indexOf(connection) === -1)
                connection.disconnect();
            else
                names[connection] = this.getField('name' + i).getValue();
        }
        this.paramsCount_ = connections.length;
        this.updateShape_();
        for (let i = 0; i < this.paramsCount_; i++) {
            if (Blockly.Mutator.reconnect(connections[i], this, 'arg' + i))
                this.getField('name' + i).setValue(names[connections[i]]);
        }
    },

    saveConnections: function(containerBlock) {
        let paramBlock = containerBlock.getInputTargetBlock('parameters');
        let i = 0;
        while (paramBlock) {
            if (paramBlock.isInsertionMarker()) {
                paramBlock = paramBlock.getNextBlock();
                continue;
            }
            const input = this.getInput('arg' + i);
            paramBlock.valueConnection_ = input && input.connection.targetConnection;
            paramBlock = paramBlock.getNextBlock();
            i++;
        }
    },

    updateShape_: function() {
        for (let i = 0; this.getInput('arg' + i); i++)
            this.removeInput('arg' + i);
        for (let i = 0; i < this.paramsCount_; i++) {
            this.appendValueInput('arg' + i).appendField(javabridge.t("blockly.block.call_procedure.with"))
                .appendField(new FieldJavaName('dependency' + i), 'name' + i).appendField(javabridge.t("blockly.block.call_procedure.arg"));
        }
    }
};
Blockly.Extensions.registerMutator('procedure_parameters_mutator', PROCEDURE_PARAMETERS_MUTATOR_MIXIN, null, ['procedure_parameter']);

Blockly.Blocks['call_procedure'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.call_procedure"))
            .appendField(new FieldDataListSelector('procedure'), 'procedure');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
        this.setMutator(new Blockly.Mutator(['procedure_parameter'], this));
        this.mixin(PROCEDURE_PARAMETERS_MUTATOR_MIXIN);
    }
};

Blockly.defineBlocksWithJsonArray([
    {
        "type": "procedure_call",
        "message0": "pass %1 %2",
        "args0": [
            {
                "type": "input_dummy"
            },
            {
                "type": "input_statement",
                "name": "parameters"
            }
        ],
        "colour": 250,
        "enableContextMenu": false
    },
    {
        "type": "procedure_parameter",
        "message0": "dependency",
        "previousStatement": null,
        "nextStatement": null,
        "colour": 250,
        "enableContextMenu": false
    }
]);