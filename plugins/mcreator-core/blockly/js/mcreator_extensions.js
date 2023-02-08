Blockly.Extensions.register('small_text_tip',
    function () {
        this.appendDummyInput().appendField(
            new Blockly.FieldLabel(javabridge.t('blockly.block.' + this.type + '.tip'), 'small-text'));
    });

Blockly.Extensions.register('gamemode_list_provider', appendDropDown('gamemodes', 'gamemode'));

Blockly.Extensions.register('damagesource_list_provider', appendDropDown('damagesources', 'damagesource'));

Blockly.Extensions.register('sound_category_list_provider',
    appendDropDownWithMessage('sound_category_list', 'soundcategories', 'soundcategory'));

Blockly.Extensions.register('material_list_provider', appendDropDown('material', 'material'));

Blockly.Extensions.register('plant_type_list_provider', appendDropDown('planttypes', 'planttype'));

Blockly.Extensions.register('gui_list_provider', appendDropDown('gui', 'guiname'));

Blockly.Extensions.register('dimension_list_provider', appendDropDown('dimension', 'dimension'));

Blockly.Extensions.register('gamerulesboolean_list_provider', appendDropDown('gamerulesboolean', 'gamerulesboolean'));

Blockly.Extensions.register('gamerulesnumber_list_provider', appendDropDown('gamerulesnumber', 'gamerulesnumber'));

Blockly.Extensions.register('schematic_list_provider', appendDropDown('schematic', 'schematic'));

Blockly.Extensions.register('fluid_list_provider', appendDropDown('fluid', 'fluid'));

Blockly.Extensions.register('direction_list_provider', appendDropDown('direction', 'direction'));

Blockly.Extensions.register('dimension_custom_list_provider', appendDropDown('dimension_custom', 'dimension'));

// Extension to mark a procedure block as a custom loop
Blockly.Extensions.register('is_custom_loop',
    function () {
        Blockly.libraryBlocks.loops.loopTypes.add(this.type);
    });

// marks in the xml if the block is attached to a block/item input, for proper mapping
Blockly.Extensions.registerMutator('mark_attached_to_block_item',
    {
        mutationToDom: function () {
            var container = document.createElement('mutation');
            var parentConnection = this.outputConnection.targetConnection;
            if (parentConnection == null)
                return null;
            else {
                var connectionChecks = parentConnection.getCheck();
                var shouldMark = connectionChecks &&
                    (connectionChecks.indexOf('MCItem') != -1 || connectionChecks.indexOf('MCItemBlock') != -1);
                container.setAttribute('mark', shouldMark);
                return container;
            }
        },

        domToMutation: function (xmlElement) {
        }
    });

// Mutator to add/remove entity input from get/set variable blocks for player variables
Blockly.Extensions.registerMutator('variable_entity_input',
    {
        mutationToDom: function () {
            var container = document.createElement('mutation');
            var isPlayerVar = javabridge.isPlayerVariable(this.getFieldValue('VAR'));
            container.setAttribute('is_player_var', isPlayerVar);
            var hasEntity = (this.getInputTargetBlock('entity') != null);
            container.setAttribute('has_entity', hasEntity);
            return container;
        },

        domToMutation: function (xmlElement) {
            var isPlayerVar = (xmlElement.getAttribute('is_player_var') == 'true');
            var hasEntity = (xmlElement.getAttribute('has_entity') == 'true');
            this.updateShape_(isPlayerVar, !hasEntity); // don't create another block if it already has one
        },

        // Helper function to add an 'entity' input to the block
        updateShape_: function (isPlayerVar, addEntityBlock) {
            var entityInput = this.getInput('entity');
            if (isPlayerVar) {
                if (!entityInput) {
                    var connection = this.appendValueInput('entity').setCheck('Entity')
                        .appendField(javabridge.t("blockly.block.var_for_entity")).connection;
                    if (addEntityBlock) {
                        var blockXML = Blockly.utils.xml.createElement('block');
                        blockXML.setAttribute('type', 'entity_from_deps');
                        var entityBlock = Blockly.Xml.domToBlock(blockXML, this.workspace);
                        connection.connect(entityBlock.outputConnection)
                    }
                }
            } else if (entityInput) {
                this.removeInput('entity');
            }
        }
    });

// Extension used by int providers to validate their min/max values, so that min can't be greater than max and vice versa
Blockly.Extensions.register('min_max_fields_validator',
    function () {
        var minField = this.getField('min');
        var maxField = this.getField('max');

        // If min > max, we set its value to that of max
        minField.setValidator(function (newValue) {
            if (newValue > maxField.getValue()) {
                return maxField.getValue();
            }
            return newValue;
        });

        // If max < min, we set its value to that of min
        maxField.setValidator(function (newValue) {
            if (newValue < minField.getValue()) {
                return minField.getValue();
            }
            return newValue;
        });
    });

// Helper function to get the min and max values of a given int provider as an array of [min, max]
function getIntProviderMinMax(providerBlock) {
    // If the int provider block is missing, return undefined
    if (!providerBlock)
        return undefined;

    // Check the value of the constant int provider
    if (providerBlock.type === 'int_provider_constant') {
        let blockValue = providerBlock.getField('value').getValue();
        return [blockValue, blockValue];
    }
    // Check the values for the other "terminal" int providers
    else if (providerBlock.type !== 'int_provider_clamped') {
        let blockMin = providerBlock.getField('min').getValue();
        let blockMax = providerBlock.getField('max').getValue();
        return [blockMin, blockMax];
    }
    // Check the values for the clamped int provider
    else {
        let blockMin = providerBlock.getField('min').getValue();
        let blockMax = providerBlock.getField('max').getValue();
        let clampedBlockMinMax = getIntProviderMinMax(providerBlock.getInput('toClamp').connection.targetBlock());
        // If the clamped block input is missing, return undefined
        if (!clampedBlockMinMax)
            return undefined;
        // Otherwise, compare the endpoints of the int provider ranges, then clamp them accordingly
        else
            return [Math.min(Math.max(blockMin, clampedBlockMinMax[0]), blockMax),
                    Math.max(Math.min(blockMax, clampedBlockMinMax[1]), blockMin)];
    }
}

// Helper function to check if the value of a given int provider is within a certain range
function isIntProviderWithinBounds(providerBlock, min, max) {
    let intProviderMinMax = getIntProviderMinMax(providerBlock);
    // If the int provider block is missing or doesn't have all the inputs, don't perform any validation
    if (!intProviderMinMax)
        return true;

    return intProviderMinMax[0] >= min && intProviderMinMax[1] <= max;
}

// Helper function for extensions that validate one or more int provider inputs
// The inputs to check and their bounds are passed as arrays of [inputName, min, max]
// The localization key of warnings is "blockly.extension.block_type.input_name"
function validateIntProviderInputs(...inputs) {
    return function () {
        this.setOnChange(function (changeEvent) {
            // Trigger the change only if a block is changed, moved, deleted or created
            if (changeEvent.type !== Blockly.Events.BLOCK_CHANGE &&
                changeEvent.type !== Blockly.Events.BLOCK_MOVE &&
                changeEvent.type !== Blockly.Events.BLOCK_DELETE &&
                changeEvent.type !== Blockly.Events.BLOCK_CREATE) {
                return;
            }
            var isValid = true;
            // For each passed input, we check if it's within bounds
            for (var i = 0; i < inputs.length; i++) {
                var countValue = this.getInput(inputs[i][0]).connection.targetBlock();
                isValid = isIntProviderWithinBounds(countValue, inputs[i][1], inputs[i][2]);
                if (!isValid)
                    break; // Stop checking as soon as one input isn't valid
            }
            if (!this.isInFlyout) {
                // Add a warning for the first non-valid input
                this.setWarningText(isValid ? null : javabridge.t('blockly.extension.' + this.type + '.' + inputs[i][0]));
                const group = Blockly.Events.getGroup();
                // Makes it so the block change and the disable event get undone together.
                Blockly.Events.setGroup(changeEvent.group);
                this.setEnabled(isValid);
                Blockly.Events.setGroup(group);
            }
        });
    };
}

Blockly.Extensions.register('count_placement_validator', validateIntProviderInputs(['count', 0, 256]));

Blockly.Extensions.register('offset_placement_validator', validateIntProviderInputs(['xz', -16, 16], ['y', -16, 16]));

Blockly.Extensions.register('delta_feature_validator', validateIntProviderInputs(['size', 0, 16], ['rimSize', 0, 16]));

Blockly.Extensions.register('replace_sphere_validator', validateIntProviderInputs(['radius', 0, 12]));

Blockly.Extensions.register('simple_column_validator', validateIntProviderInputs(['height', 0, Infinity]));

// Helper function to provide a mixin for mutators that add a single repeating input
// The mutator container block must have a "STACK" statement input for this to work
// The input/empty messages are localized as "blockly.block.block_type.input" and "blockly.block.block_type.empty"
function simpleRepeatingInputMixin(mutatorContainer, mutatorInput, inputName, inputType) {
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
        saveExtraState: function() {
            return {
                'inputCount': this.inputCount_
            };
        },

        // Retrieve number of inputs from JSON
        loadExtraState: function(state) {
            this.inputCount_ = state['inputCount'];
            this.updateShape_();
        },

        // "Split" this block into the correct number of inputs in the mutator UI
        decompose: function(workspace) {
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
        compose: function(containerBlock) {
            let inputBlock = containerBlock.getInputTargetBlock('STACK');
            // Count number of inputs.
            const connections = [];
            while (inputBlock && !inputBlock.isInsertionMarker()) {
                connections.push(inputBlock.valueConnection_);
                inputBlock = inputBlock.nextConnection && inputBlock.nextConnection.targetBlock();
            }
            // Disconnect any children that don't belong.
            for (let i = 0; i < this.inputCount_; i++) {
                const connection = this.getInput(inputName + i) && this.getInput(inputName + i).connection.targetConnection;
                if (connection && connections.indexOf(connection) == -1) {
                    connection.disconnect();
                }
            }
            this.inputCount_ = connections.length;
            this.updateShape_();
            // Reconnect any child blocks.
            for (let i = 0; i < this.inputCount_; i++) {
                Blockly.Mutator.reconnect(connections[i], this, inputName + i);
            }
        },

        // Keep track of the connected blocks, so that they don't get disconnected whenever an input is added or moved
        saveConnections: function(containerBlock) {
            let inputBlock = containerBlock.getInputTargetBlock('STACK');
            let i = 0;
            while (inputBlock) {
                if (inputBlock.isInsertionMarker()) {
                    inputBlock = inputBlock.getNextBlock();
                    continue;
                }
                const input = this.getInput(inputName + i);
                inputBlock.valueConnection_ = input && input.connection.targetConnection;
                inputBlock = inputBlock.getNextBlock();
                i++;
            }
        },

        // Add/remove inputs from this block
        updateShape_: function() {
            // Handle the dummy "empty" input for when there are no proper inputs
            if (this.inputCount_ && this.getInput('EMPTY')) {
                this.removeInput('EMPTY');
            } else if (!this.inputCount_ && !this.getInput('EMPTY')) {
                this.appendDummyInput('EMPTY').appendField(javabridge.t('blockly.block.' + this.type + '.empty'));
            }
            // Add proper inputs
            for (let i = 0; i < this.inputCount_; i++) {
                if (!this.getInput(inputName + i))
                    this.appendValueInput(inputName + i).setCheck(inputType).setAlign(Blockly.Input.Align.RIGHT)
                            .appendField(javabridge.t('blockly.block.' + this.type + '.input'));
            }
            // Remove extra inputs
            for (let i = this.inputCount_; this.getInput(inputName + i); i++) {
                this.removeInput(inputName + i);
            }
        }
    }
}

Blockly.Extensions.registerMutator('block_predicate_all_any_mutator', simpleRepeatingInputMixin(
        'block_predicate_mutator_container', 'block_predicate_mutator_input', 'condition', 'BlockPredicate'),
        undefined, ['block_predicate_mutator_input']);

// Helper function for extensions that validate one or more resource location text fields
function validateResourceLocationFields(...fields) {
    return function() {
        for (let i = 0; i < fields.length; i++) {
            let field = this.getField(fields[i]);
            // The validator checks if the new input value is a valid resource location
            field.setValidator(function(newValue) {
                if (/^([a-z0-9_\-\.]+:)?[a-z0-9_\-\.\/]+$/.test(newValue))
                    return newValue;
                return null;
            });
        }
    }
}

Blockly.Extensions.register('holder_set_tag_validator', validateResourceLocationFields('tag'));