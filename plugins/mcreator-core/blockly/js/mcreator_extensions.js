Blockly.Extensions.register('small_text_tip',
    function () {
        this.appendDummyInput().appendField(
            new Blockly.FieldLabel(javabridge.t('blockly.block.' + this.type + '.tip'), 'small-text'));
    });

// Extension to mark a procedure block as a custom loop
Blockly.Extensions.register('is_custom_loop',
    function () {
        Blockly.libraryBlocks.loops.loopTypes.add(this.type);
    });

// Extension to append the marker image to all blockstate provider inputs
Blockly.Extensions.register('add_image_to_bsp_inputs',
    function () {
        for (let i = 0, input; input = this.inputList[i]; i++) {
            if (input.connection && input.connection.getCheck() && input.connection.getCheck()[0] == 'BlockStateProvider')
                input.appendField(new Blockly.FieldImage("./res/bsp_input.png", 8, 20));
        }
    });

// Extension to append the marker image to all plain blockstate inputs
Blockly.Extensions.register('add_image_to_blockstate_inputs',
    function () {
        for (let i = 0, input; input = this.inputList[i]; i++) {
            if (input.connection && input.connection.getCheck() && input.connection.getCheck()[0] == 'MCItemBlock')
                input.appendField(new Blockly.FieldImage("./res/b_input.png", 8, 10));
        }
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

// Helper function to use in Blockly extensions that register one data list selector field to update contents of another
// The block may define input called "<targetName>Field" to customize field's position
// Note that the source field must be inserted before the target field for their values to be loaded properly
function appendAutoReloadingDataListField(sourceName, targetName, targetList) {
    return function () {
        const thisBlock = this;
        (this.getInput(targetName + 'Field') || this.appendDummyInput()).appendField(
            new FieldDataListSelector(targetList, undefined, {
                'customEntryProviders': function () {
                    return thisBlock.getFieldValue(sourceName);
                }
            }), targetName);
        this.setOnChange(function (changeEvent) {
            // Proceed if event represents change to field named "<sourceName>" on this block and was created in a group
            // Event triggered by FieldDataListSelector is only grouped if field value is modified in UI
            if (changeEvent.type === Blockly.Events.BLOCK_CHANGE &&
                changeEvent.group && changeEvent.blockId === this.id &&
                changeEvent.element === 'field' &&
                changeEvent.name === sourceName) {
                const group = Blockly.Events.getGroup();
                // Makes it so the update and the reset event get undone together.
                Blockly.Events.setGroup(changeEvent.group);
                this.setFieldValue('', targetName);
                Blockly.Events.setGroup(group);
            }
        });
    };
}

Blockly.Extensions.register('entity_data_logic_list_provider',
    appendAutoReloadingDataListField('customEntity', 'accessor', 'entitydata_logic'));

Blockly.Extensions.register('entity_data_integer_list_provider',
    appendAutoReloadingDataListField('customEntity', 'accessor', 'entitydata_integer'));

Blockly.Extensions.register('entity_data_string_list_provider',
    appendAutoReloadingDataListField('customEntity', 'accessor', 'entitydata_string'));

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

// Mutator to disable the "biome filter" placement inside the "inline placed feature" block
Blockly.Extensions.registerMixin('disable_inside_inline_placed_feature',
    {
        // Check if this block is inside the inline placed feature statement
        getSurroundLoop: function () {
            let block = this;
            do {
                if (block.type == 'placed_feature_inline') {
                    return block;
                }
                block = block.getSurroundParent();
            } while (block);
            return null;
        },

        onchange: function (e) {
            // Don't change state if it's at the start of a drag and it's not a move event
            if (!this.workspace.isDragging || this.workspace.isDragging() || e.type !== Blockly.Events.BLOCK_MOVE) {
                return;
            }
            const enabled = !(this.getSurroundLoop(this));
            this.setWarningText(enabled ? null : javabridge.t('blockly.block.placed_feature_inline.disabled_placement'));
            if (!this.isInFlyout) {
                const group = Blockly.Events.getGroup();
                // Makes it so the move and the disable event get undone together.
                Blockly.Events.setGroup(e.group);
                this.setEnabled(enabled);
                Blockly.Events.setGroup(group);
            }
        }
    });

// Helper function for extensions that validate one or more resource location text fields
function validateResourceLocationFields(...fields) {
    return function () {
        for (let i = 0; i < fields.length; i++) {
            let field = this.getField(fields[i]);
            // The validator checks if the new input value is a valid resource location
            field.setValidator(function (newValue) {
                if (/^([a-z0-9_\-\.]+:)?[a-z0-9_\-\.\/]+$/.test(newValue))
                    return newValue;
                return null;
            });
        }
    }
}

Blockly.Extensions.register('tag_input_field_validator', validateResourceLocationFields('tag'));

Blockly.Extensions.register('geode_tag_fields_validator',
    validateResourceLocationFields('cannot_replace_tag', 'invalid_blocks_tag'));