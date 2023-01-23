const PROCEDURE_DEPENDENCIES_MUTATOR_MIXIN = {
    depCount_: 0,

    mutationToDom: function() {
        const container = Blockly.utils.xml.createElement('mutation');
        container.setAttribute('dependencies', this.depCount_);
        return container;
    },

    domToMutation: function(xmlElement) {
        this.depCount_ = parseInt(xmlElement.getAttribute('dependencies'), 10);
        this.updateShape_();
    },

    saveExtraState: function() {
        return {
            'depCount': this.depCount_
        };
    },

    loadExtraState: function(state) {
        this.depCount_ = state['depCount'];
        this.updateShape_();
    },

    decompose: function(workspace) {
        const containerBlock = workspace.newBlock('procedure_call');
        containerBlock.initSvg();
        let connection = containerBlock.getInput('STACK').connection;
        for (let i = 1; i <= this.depCount_; i++) {
            const depBlock = workspace.newBlock('procedure_dependency');
            depBlock.initSvg();
            connection.connect(depBlock.previousConnection);
            connection = depBlock.nextConnection;
        }
        return containerBlock;
    },

    compose: function(containerBlock) {
        let depBlock = containerBlock.getInputTargetBlock('STACK');
        const connections = [];
        while (depBlock) {
            if (!depBlock.isInsertionMarker())
                connections.push(depBlock.valueConnection_);
            depBlock = depBlock.getNextBlock();
        }
        const names = {};
        for (let i = 0; i < this.depCount_; i++) {
            const connection = this.getInput('arg' + i).connection.targetConnection;
            if (connection && connections.indexOf(connection) === -1)
                connection.disconnect();
            else
                names[connection] = this.getField('name' + i).getValue();
        }
        this.depCount_ = connections.length;
        this.updateShape_();
        for (let i = 0; i < this.depCount_; i++) {
            if (Blockly.Mutator.reconnect(connections[i], this, 'arg' + i))
                this.getField('name' + i).setValue(names[connections[i]]);
        }
    },

    saveConnections: function(containerBlock) {
        let depBlock = containerBlock.getInputTargetBlock('STACK');
        let i = 0;
        while (depBlock) {
            if (depBlock.isInsertionMarker()) {
                depBlock = depBlock.getNextBlock();
                continue;
            }
            const input = this.getInput('arg' + i);
            depBlock.valueConnection_ = input && input.connection.targetConnection;
            depBlock = depBlock.getNextBlock();
            i++;
        }
    },

    updateShape_: function() {
        for (let i = 0; this.getInput('arg' + i); i++)
            this.removeInput('arg' + i);
        for (let i = 0; i < this.depCount_; i++) {
            this.appendValueInput('arg' + i).appendField(javabridge.t("blockly.block.call_procedure.with"))
                .appendField(new FieldJavaName('dependency' + i), 'name' + i).appendField(javabridge.t("blockly.block.call_procedure.arg"));
        }
    }
};
Blockly.Extensions.registerMutator('procedure_dependencies_mutator', PROCEDURE_DEPENDENCIES_MUTATOR_MIXIN, null, ['procedure_dependency']);

Blockly.Blocks['call_procedure'] = {
    init: function () {
        this.appendDummyInput()
            .appendField(javabridge.t("blockly.block.call_procedure"))
            .appendField(new FieldDataListSelector('procedure'), 'procedure');
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(250);
        this.setMutator(new Blockly.Mutator(['procedure_dependency'], this));
        this.mixin(PROCEDURE_DEPENDENCIES_MUTATOR_MIXIN);
    }
};

Blockly.Blocks['procedure_call'] = {
    init: function() {
        this.appendDummyInput().appendField(javabridge.t('blockly.block.call_procedure.container'));
        this.appendStatementInput('STACK');
        this.contextMenu = false;
        this.setColour(250);
    }
};

Blockly.Blocks['procedure_dependency'] = {
    init: function() {
        this.appendDummyInput().appendField(javabridge.t('blockly.block.call_procedure.input'));
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.contextMenu = false;
        this.setColour(250);
    }
};