Blockly.Extensions.register('procedure_dependencies_tooltip',
    function () {
        let thisBlock = this;
        this.setTooltip(function () {
            const depList = javabridge.getDependencies(thisBlock.getFieldValue('procedure'));
            if (depList.length === 0)
                return javabridge.t('blockly.extension.procedure_dep_tooltip.empty');
            let tooltip = javabridge.t('blockly.extension.procedure_dep_tooltip');
            for (const dependency of depList)
                tooltip += '\n' + dependency;
            return tooltip;
        });
    });

Blockly.Extensions.register('procedure_dependencies_onchange_mixin',
    function () {
        this.setOnChange(function (changeEvent) {
            // Trigger the change only if a block is changed (fields only), moved, deleted or created
            if ((changeEvent.type !== Blockly.Events.BLOCK_CHANGE || changeEvent.element !== 'field') &&
                changeEvent.type !== Blockly.Events.BLOCK_MOVE &&
                changeEvent.type !== Blockly.Events.BLOCK_DELETE &&
                changeEvent.type !== Blockly.Events.BLOCK_CREATE) {
                return;
            }
            const group = Blockly.Events.getGroup();
            // Makes it so the block change and the unplug event get undone together.
            Blockly.Events.setGroup(changeEvent.group);
            const depList = javabridge.getDependencies(this.getFieldValue('procedure'));
            for (let i = 0; this.getField('name' + i); i++) {
                const prevType = this.getInput('arg' + i).connection.getCheck();
                let depType = null;
                for (const dep of depList) {
                    if (dep.getName() === this.getFieldValue('name' + i)) {
                        depType = dep.getBlocklyType();
                        if (depType === '')
                            depType = [];
                        break;
                    }
                }
                // Set input checks from dependency type
                this.getInput('arg' + i).setCheck(depType);
                const newType = this.getInput('arg' + i).connection.getCheck();
                // Fire change event if block existed earlier and previous input type was different
                // This is the reason we check changeEvent.element above
                if (changeEvent.type === Blockly.Events.BLOCK_CHANGE &&
                    JSON.stringify(prevType) !== JSON.stringify(newType)) {
                    const inputCheckChange = new Blockly.Events.BlockChange(this, null, 'arg' + i, prevType, newType);
                    inputCheckChange.run = function (forward) {
                        const block = this.blockId && this.getEventWorkspace_().getBlockById(this.blockId);
                        if (block)
                            block.getInput(this.name).setCheck(forward ? this.newValue : this.oldValue);
                    };
                    Blockly.Events.fire(inputCheckChange);
                }
            }
            Blockly.Events.setGroup(group);
        });
    });

// Helper function to use in Blockly extensions that validate repeating fields' values meant to be unique
function uniqueValueValidator(fieldName) {
    return function (newValue) {
        for (let i = 0; this.sourceBlock_.getField(fieldName + i); i++) {
            if (this.sourceBlock_.getFieldValue(fieldName + i) === newValue && (fieldName + i) !== this.name)
                return null;
        }
        return newValue;
    };
}

Blockly.Extensions.registerMutator('procedure_dependencies_mutator', {
    // Store number of inputs in XML as '<mutation inputs="inputCount_"></mutation>'
    mutationToDom: function () {
        const container = document.createElement('mutation');
        container.setAttribute('inputs', this.inputCount_);
        return container;
    },

    // Retrieve number of inputs from XML
    domToMutation: function (xmlElement) {
        this.inputCount_ = isNaN(xmlElement.getAttribute('inputs')) ?
            0 : // If block existed before without this mutator, initialize the property with default value
            parseInt(xmlElement.getAttribute('inputs'), 10);
        this.updateShape_();
    },

    // Store number of inputs in JSON
    saveExtraState: function () {
        return {
            'inputCount': this.inputCount_
        };
    },

    // Retrieve number of inputs from JSON
    loadExtraState: function (state) {
        this.inputCount_ = state['inputCount'];
        this.updateShape_();
    },

    // "Split" this block into the correct number of inputs in the mutator UI
    decompose: function (workspace) {
        const containerBlock = workspace.newBlock('procedure_dependencies_mutator_container');
        containerBlock.initSvg();
        let connection = containerBlock.getInput('STACK').connection;
        for (let i = 0; i < this.inputCount_; i++) {
            const inputBlock = workspace.newBlock('procedure_dependencies_mutator_input');
            inputBlock.nameValue_ = null;
            inputBlock.initSvg();
            connection.connect(inputBlock.previousConnection);
            connection = inputBlock.nextConnection;
        }
        return containerBlock;
    },

    // Rebuild this block based on the number of inputs in the mutator UI
    compose: function (containerBlock) {
        let inputBlock = containerBlock.getInputTargetBlock('STACK');
        const connections = [];
        const fieldValues = {};
        const fieldValuesDummy = []; // Names of dependency inputs (field values) that don't have any block attached
        while (inputBlock && !inputBlock.isInsertionMarker()) {
            connections.push(inputBlock.valueConnection_);
            if (inputBlock.valueConnection_)
                fieldValues[inputBlock.valueConnection_.sourceBlock_.id] = inputBlock.nameValue_;
            else
                fieldValuesDummy.push(inputBlock.nameValue_);
            inputBlock = inputBlock.nextConnection && inputBlock.nextConnection.targetBlock();
        }
        // Disconnect any children that don't belong
        for (let i = 0; i < this.inputCount_; i++) {
            const connection = this.getInput('arg' + i) && this.getInput('arg' + i).connection.targetConnection;
            if (connection && connections.indexOf(connection) === -1)
                connection.disconnect();
        }
        this.inputCount_ = connections.length;
        this.updateShape_();

        // Find first index not used by default dependency input names (dependency0, dependency1, ...)
        const fieldValuesFlat = Object.values(fieldValues).concat(fieldValuesDummy);
        let defNameIndex = 0;
        while (fieldValuesFlat.indexOf('dependency' + defNameIndex) !== -1)
            defNameIndex++;

        // Reconnect any child blocks and update values
        for (let i = 0, dummyIndex = 0; i < this.inputCount_; i++) {
            Blockly.Mutator.reconnect(connections[i], this, 'arg' + i);
            const currentField = this.getField('name' + i);

            // Remove validator to avoid validation errors as we set new values
            const validator = currentField.getValidator();
            currentField.setValidator(null);

            // If input block is attached, the associated name is restored
            if (connections[i])
                currentField.setValue(fieldValues[connections[i].sourceBlock_.id] || '');

            // If input is empty or associated name was not found, new name is set
            // Which is one of collected "dummy" names if some are still not processed or default name otherwise
            if (!connections[i] || currentField.getValue() === '') {
                currentField.setValue((dummyIndex < fieldValuesDummy.length && fieldValuesDummy[dummyIndex++]) ||
                    'dependency' + (defNameIndex++));
            }

            // Restore validator
            currentField.setValidator(validator);
        }
    },

    // Keep track of the connected blocks, so that they don't get disconnected whenever an input is added or moved
    saveConnections: function (containerBlock) {
        let inputBlock = containerBlock.getInputTargetBlock('STACK');
        let i = 0;
        while (inputBlock) {
            if (!inputBlock.isInsertionMarker()) {
                const input = this.getInput('arg' + i);
                if (input) {
                    inputBlock.valueConnection_ = input.connection.targetConnection;
                    inputBlock.nameValue_ = this.getFieldValue('name' + i);
                }
                i++;
            }
            inputBlock = inputBlock.getNextBlock();
        }
    },

    // Add/remove inputs from this block
    updateShape_: function () {
        for (let i = 0; i < this.inputCount_; i++) {
            if (!this.getInput('arg' + i)) {
                this.appendValueInput('arg' + i).setAlign(Blockly.Input.Align.RIGHT)
                    .appendField(javabridge.t('blockly.block.call_procedure.name'))
                    .appendField(new FieldJavaName('', uniqueValueValidator('name')), 'name' + i)
                    .appendField(javabridge.t('blockly.block.call_procedure.arg'));
            }
        }
        for (let i = this.inputCount_; this.getInput('arg' + i); i++)
            this.removeInput('arg' + i);
    }
}, undefined, ['procedure_dependencies_mutator_input']);
