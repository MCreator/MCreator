// Helper function to provide a mixin for mutators that add a single repeating (dummy) input with additional fields
// The mutator container block must have a "STACK" statement input for this to work
// The empty message is localized as "blockly.block.block_type.empty"
// The input provider is a function that accepts the block being mutated, the input name and the input index
// If the input provider returns a dummy input (i.e. only repeating fields are being added), isProperInput must be set to false
function simpleRepeatingInputMixin(mutatorContainer, mutatorInput, inputName, inputProvider, isProperInput = true,
        fieldNames = [], disableIfEmpty) {
    return {
        // Store number of inputs in XML as '<mutation inputs="inputCount_"></mutation>'
        mutationToDom: function () {
            var container = document.createElement('mutation');
            container.setAttribute('inputs', this.inputCount_);
            return container;
        },

        // Retrieve number of inputs from XML
        domToMutation: function (xmlElement) {
            this.inputCount_ = parseInt(xmlElement.getAttribute('inputs'), 10);
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
            const containerBlock = workspace.newBlock(mutatorContainer);
            containerBlock.initSvg();
            var connection = containerBlock.getInput('STACK').connection;
            for (let i = 0; i < this.inputCount_; i++) {
                const inputBlock = workspace.newBlock(mutatorInput);
                inputBlock.initSvg();
                connection.connect(inputBlock.previousConnection);
                connection = inputBlock.nextConnection;
            }
            return containerBlock;
        },

        // Rebuild this block based on the number of inputs in the mutator UI
        compose: function (containerBlock) {
            let inputBlock = containerBlock.getInputTargetBlock('STACK');
            // Count number of inputs.
            const connections = [];
            const fieldValues = [];
            while (inputBlock && !inputBlock.isInsertionMarker()) {
                connections.push(inputBlock.valueConnection_);
                fieldValues.push(inputBlock.fieldValues_);
                inputBlock = inputBlock.nextConnection && inputBlock.nextConnection.targetBlock();
            }
            // Disconnect any children that don't belong. This is skipped if the provided input is a dummy input
            if (isProperInput) {
                for (let i = 0; i < this.inputCount_; i++) {
                    const connection = this.getInput(inputName + i) && this.getInput(inputName + i).connection.targetConnection;
                    if (connection && connections.indexOf(connection) == -1) {
                        connection.disconnect();
                    }
                }
            }
            this.inputCount_ = connections.length;
            this.updateShape_();
            // Reconnect any child blocks and update the field values
            for (let i = 0; i < this.inputCount_; i++) {
                if (isProperInput) {
                    Blockly.Mutator.reconnect(connections[i], this, inputName + i);
                }
                if (fieldValues[i]) {
                    for (let j = 0; j < fieldNames.length; j++) {
                        // If this is a new field, then keep its initial value, otherwise assign the stored value
                        if (fieldValues[i][j] != null)
                            this.getField(fieldNames[j] + i).setValue(fieldValues[i][j]);
                    }
                }
            }
        },

        // Keep track of the connected blocks, so that they don't get disconnected whenever an input is added or moved
        // This also keeps track of the field values
        saveConnections: function (containerBlock) {
            let inputBlock = containerBlock.getInputTargetBlock('STACK');
            let i = 0;
            while (inputBlock) {
                if (!inputBlock.isInsertionMarker()) {
                    const input = this.getInput(inputName + i);
                    if (input) {
                        if (isProperInput) {
                            inputBlock.valueConnection_ = input.connection.targetConnection;
                        }
                        inputBlock.fieldValues_ = [];
                        for (let j = 0; j < fieldNames.length; j++) {
                            const currentFieldName = fieldNames[j] + i;
                            inputBlock.fieldValues_[j] = this.getFieldValue(currentFieldName);
                        }
                    }
                    i++;
                }
                inputBlock = inputBlock.getNextBlock();
            }
        },

        // Add/remove inputs from this block
        updateShape_: function () {
            this.handleEmptyInput_(disableIfEmpty);
            // Add proper inputs
            for (let i = 0; i < this.inputCount_; i++) {
                if (!this.getInput(inputName + i))
                    inputProvider(this, inputName, i);
            }
            // Remove extra inputs
            for (let i = this.inputCount_; this.getInput(inputName + i); i++) {
                this.removeInput(inputName + i);
            }
        },

        // Handle the dummy "empty" input or warning for when there are no proper inputs
        handleEmptyInput_: function (disableIfEmpty) {
            if (disableIfEmpty === undefined) {
                if (this.inputCount_ && this.getInput('EMPTY')) {
                    this.removeInput('EMPTY');
                } else if (!this.inputCount_ && !this.getInput('EMPTY')) {
                    this.appendDummyInput('EMPTY').appendField(javabridge.t('blockly.block.' + this.type + '.empty'));
                }
            } else if (disableIfEmpty) {
                this.setWarningText(this.inputCount_ ? null : javabridge.t('blockly.block.' + this.type + '.empty'));
                this.setEnabled(this.inputCount_);
            }
        }
    }
}

// Helper function to provide mixins for weighted list mutators
function weightedListMutatorMixin(inputType) {
    return simpleRepeatingInputMixin('weighted_list_mutator_container', 'weighted_list_mutator_input', 'entry',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck(inputType).setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t('blockly.block.weighted_list.weight'))
                .appendField(new Blockly.FieldNumber(1, 1, null, 1), 'weight' + index)
                .appendField(javabridge.t('blockly.block.weighted_list.entry'));
        }, true, ['weight'], true);
}

Blockly.Extensions.registerMutator('block_predicate_all_any_mutator', simpleRepeatingInputMixin(
        'block_predicate_mutator_container', 'block_predicate_mutator_input', 'condition',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck('BlockPredicate').setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t('blockly.block.' + thisBlock.type + '.input'));
        }),
    undefined, ['block_predicate_mutator_input']);

Blockly.Extensions.registerMutator('feature_simple_random_mutator', simpleRepeatingInputMixin(
        'feature_simple_random_mutator_container', 'feature_simple_random_mutator_input', 'feature',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck(['Feature', 'PlacedFeature'])
                .setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t('blockly.block.' + thisBlock.type + '.input'));
        }, true, [], true),
    undefined, ['feature_simple_random_mutator_input']);

Blockly.Extensions.registerMutator('block_list_mutator', simpleRepeatingInputMixin(
        'block_list_mutator_container', 'block_list_mutator_input', 'condition',
        function (thisBlock, inputName, index) {
            thisBlock.appendDummyInput(inputName + index).setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t('blockly.block.' + thisBlock.type + '.input'))
                .appendField(new FieldMCItemSelector('allblocks'), 'block' + index);
        }, false, ['block']),
    undefined, ['block_list_mutator_input']);

Blockly.Extensions.registerMutator('geode_crystal_mutator', simpleRepeatingInputMixin(
        'geode_crystal_mutator_container', 'geode_crystal_mutator_input', 'crystal',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck('MCItemBlock')
                .appendField(javabridge.t('blockly.block.' + thisBlock.type + '.input'))
                .appendField(new Blockly.FieldImage("./res/b_input.png", 8, 10));
        }, true, [], true),
    undefined, ['geode_crystal_mutator_input']);

Blockly.Extensions.registerMutator('ore_feature_mutator', simpleRepeatingInputMixin(
        'ore_mutator_container', 'ore_mutator_input', 'target',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck('OreTarget').setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t(
                    index == 0 ? 'blockly.block.ore_mutator.try' : 'blockly.block.ore_mutator.else_try'));
        }),
    undefined, ['ore_mutator_input']);

Blockly.Extensions.registerMutator('weighted_height_provider_mutator', weightedListMutatorMixin('HeightProvider'),
    undefined, ['weighted_list_mutator_input']);

Blockly.Extensions.registerMutator('weighted_int_provider_mutator', weightedListMutatorMixin('IntProvider'),
    undefined, ['weighted_list_mutator_input']);

// We cannot use the weighted mutator function, as we need to add image fields too
Blockly.Extensions.registerMutator('weighted_state_provider_mutator', simpleRepeatingInputMixin(
        'weighted_list_mutator_container', 'weighted_list_mutator_input', 'entry',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck('MCItemBlock').setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t('blockly.block.weighted_list.weight'))
                .appendField(new Blockly.FieldNumber(1, 1, null, 1), 'weight' + index)
                .appendField(javabridge.t('blockly.block.weighted_list.entry'))
                .appendField(new Blockly.FieldImage("./res/b_input.png", 8, 10));
        }, true, ['weight'], true),
    undefined, ['weighted_list_mutator_input']);

// Mutator for repeating tree decorator inputs
Blockly.Extensions.registerMutator('tree_decorator_mutator', simpleRepeatingInputMixin(
        'tree_decorator_mutator_container', 'tree_decorator_mutator_input', 'decorator',
        function (thisBlock, inputName, index) {
            thisBlock.appendValueInput(inputName + index).setCheck('TreeDecorator').setAlign(Blockly.Input.Align.RIGHT)
                .appendField(javabridge.t('blockly.block.feature_tree.decorator_input'));
        }),
    undefined, ['tree_decorator_mutator_input']);
