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
        mutationToDom: function() {
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

        domToMutation: function(xmlElement) {}
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