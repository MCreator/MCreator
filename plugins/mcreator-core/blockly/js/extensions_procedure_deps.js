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

// Helper function to find first index not taken by repeating fields with a certain name
// If index is defined, field with given name with that index will be skipped
// If valueProvider is defined, its return value is compared with other field values, otherwise index itself is used
function firstFreeIndex(block, fieldName, index, valueProvider) {
    const values = [];
    for (let i = 0; block.getField(fieldName + i); i++) {
        if (index && i === index)
            continue;
        values.push('' + block.getFieldValue(fieldName + i));
    }
    let retVal = 0;
    while (true) {
        if (values.indexOf('' + (valueProvider ? valueProvider(retVal) : retVal)) === -1)
            break;
        retVal++;
    }
    return valueProvider ? valueProvider(retVal) : retVal;
}

Blockly.Extensions.registerMutator('procedure_dependencies_mutator', {
    mutationToDom: function () {
        const container = document.createElement('mutation');
        container.setAttribute('inputs', this.inputCount_);
        return container;
    },

    domToMutation: function (xmlElement) {
        this.inputCount_ = parseInt(xmlElement.getAttribute('inputs'), 10);
        this.updateShape_();
    },

    saveExtraState: function () {
        return {
            'inputCount': this.inputCount_
        };
    },

    loadExtraState: function (state) {
        this.inputCount_ = state['inputCount'];
        this.updateShape_();
    },

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

    compose: function (containerBlock) {
        let inputBlock = containerBlock.getInputTargetBlock('STACK');
        const connections = [];
        const fieldValues = {};
        const fieldValuesDummy = [];
        while (inputBlock && !inputBlock.isInsertionMarker()) {
            connections.push(inputBlock.valueConnection_);
            if (inputBlock.valueConnection_)
                fieldValues[inputBlock.valueConnection_.sourceBlock_.id] = inputBlock.nameValue_;
            else
                fieldValuesDummy.push(inputBlock.nameValue_);
            inputBlock = inputBlock.nextConnection && inputBlock.nextConnection.targetBlock();
        }
        for (let i = 0; i < this.inputCount_; i++) {
            const connection = this.getInput('arg' + i) && this.getInput('arg' + i).connection.targetConnection;
            if (connection && connections.indexOf(connection) === -1)
                connection.disconnect();
        }
        this.inputCount_ = connections.length;
        this.updateShape_();
        const fieldValuesFlat = Object.values(fieldValues).concat(fieldValuesDummy);
        let k = 0;
        while (fieldValuesFlat.indexOf('dependency' + k) !== -1)
            k++;
        for (let i = 0, j = 0; i < this.inputCount_; i++) {
            Blockly.Mutator.reconnect(connections[i], this, 'arg' + i);
            const currentField = this.getField('name' + i);
            const validator = currentField.getValidator();
            currentField.setValidator(null);
            if (connections[i])
                currentField.setValue(fieldValues[connections[i].sourceBlock_.id] || '');
            if (!connections[i] || currentField.getValue() === '')
                currentField.setValue((j < fieldValuesDummy.length && fieldValuesDummy[j++]) || 'dependency' + (k++));
            currentField.setValidator(validator);
        }
    },

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
