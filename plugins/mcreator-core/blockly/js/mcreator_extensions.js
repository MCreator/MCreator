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

// Helper function to check if the value of a given int provider is within a certain range
function isIntProviderWithinBounds(providerBlock, min, max) {
    // If the int provider block is missing, don't perform any validation
    if (!providerBlock)
        return true;

    // Check the value of the constant int provider
    if (providerBlock.type === 'int_provider_constant') {
        let blockValue = providerBlock.getField('value').getValue();
        return blockValue >= min && blockValue <= max;
    }
    // Check the values for the other "terminal" int providers
    else if (providerBlock.type !== 'int_provider_clamped') {
        let blockMin = providerBlock.getField('min').getValue();
        let blockMax = providerBlock.getField('max').getValue();
        return blockMin >= min && blockMax <= max;
    }
    // Check the values for the clamped int provider
    else {
        let blockMin = providerBlock.getField('min').getValue();
        let blockMax = providerBlock.getField('max').getValue();
        let clampedBlock = providerBlock.getInput('toClamp').connection.targetBlock();
        // If the input block is being clamped within bounds, stop checking. Otherwise, check the input as well
        return (blockMin >= min && blockMax <= max) || isIntProviderWithinBounds(clampedBlock, min, max);
    }
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
        const containerBlock = workspace.newBlock('call_procedure_mutator_container');
        containerBlock.initSvg();
        let connection = containerBlock.getInput('dependencies').connection;
        for (let i = 1; i <= this.depCount_; i++) {
            const depBlock = workspace.newBlock('call_procedure_mutator_input');
            depBlock.initSvg();
            connection.connect(depBlock.previousConnection);
            connection = depBlock.nextConnection;
        }
        return containerBlock;
    },

    compose: function(containerBlock) {
        let depBlock = containerBlock.getInputTargetBlock('dependencies');
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
        let depBlock = containerBlock.getInputTargetBlock('dependencies');
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
Blockly.Extensions.registerMutator('procedure_dependencies_mutator', PROCEDURE_DEPENDENCIES_MUTATOR_MIXIN, null, ['call_procedure_mutator_input']);