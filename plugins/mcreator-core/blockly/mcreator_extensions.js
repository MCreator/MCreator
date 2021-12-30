Blockly.Extensions.register('biome_dictionary_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("biomedictionarytypes"))), 'biomedict');
    });

Blockly.Extensions.register('gamemode_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamemodes"))), 'gamemode');
    });

Blockly.Extensions.register('damagesource_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("damagesources"))), 'damagesource');
    });

Blockly.Extensions.register('sound_category_list_provider',
    function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.extension.sound_category_list"))
            .appendField(new Blockly.FieldDropdown(
                arrayToBlocklyDropDownArray(javabridge.getListOf("soundcategories"))), 'soundcategory');
    });

Blockly.Extensions.register('material_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("material"))), 'material');
    });

Blockly.Extensions.register('plant_type_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("planttypes"))), 'planttype');
    });

// Extension to mark a procedure block as a custom loop
Blockly.Extensions.register('is_custom_loop',
    function () {
        Blockly.Constants.Loops.CONTROL_FLOW_IN_LOOP_CHECK_MIXIN.LOOP_TYPES.push(this.type);
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

Blockly.Extensions.register('biome_list_provider',
    function () {
        this.appendDummyInput().appendField(new FieldDataListSelector('biome'), 'biome');
    });

Blockly.Extensions.register('entity_list_provider',
    function () {
        this.appendDummyInput().appendField(new FieldDataListSelector('entity'), 'entity');
    });

Blockly.Extensions.register('gui_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gui"))), 'guiname');
    });

Blockly.Extensions.register('rangeditem_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("rangeditem"))), 'rangeditem');
    });

Blockly.Extensions.register('dimension_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("dimension"))), 'dimension');
    });

Blockly.Extensions.register('achievement_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("achievement"))), 'achievement');
    });

Blockly.Extensions.register('effect_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("effect"))), 'potion'); // field name is potion for legacy reasons
    });

Blockly.Extensions.register('potion_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("potion"))), 'potionitem');
    });

Blockly.Extensions.register('gamerulesboolean_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamerulesboolean"))), 'gamerulesboolean');
    });

Blockly.Extensions.register('gamerulesnumber_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamerulesnumber"))), 'gamerulesnumber');
    });

Blockly.Extensions.register('enhancement_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("enhancement"))), 'enhancement');
    });

Blockly.Extensions.register('sound_list_provider',
    function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.extension.sound_list"))
            .appendField(new FieldDataListSelector('sound'), 'sound');
    });

Blockly.Extensions.register('particle_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("particle"))), 'particle');
    });

Blockly.Extensions.register('schematic_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("schematic"))), 'schematic');
    });

Blockly.Extensions.register('fluid_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("fluid"))), 'fluid');
    });

Blockly.Extensions.register('direction_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("direction"))), 'direction');
    });

Blockly.Extensions.register('dimension_custom_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("dimension_custom"))), 'dimension');
    });

/**
 * This function provides a label field that can be double-clicked to open a list entry selector.
 * The behaviour is similar to block/item selectors or condition selectors for entity AI blocks
 */
function FieldDataListSelector(type) {
    // The default entry is ",No entry selected". Since the value is an empty string, the procedure editor will show a compile error
    let getDefaultEntry = function () {
        return ',' + javabridge.t('blockly.extension.data_list_selector.no_entry');
    }

    // While the procedure is open, we store the selected entry as a "value,readableName" pair
    let entry = getDefaultEntry();

    // The clickable part of the custom field
    let entryField = new Blockly.FieldLabelSerializable(javabridge.t('blockly.extension.data_list_selector.no_entry'), 'entry-label');
    entryField.EDITABLE = true;
    entryField.SERIALIZABLE = true;

    // Initialize the label with a rectangle surrounding the text
    entryField.initView = function () {
        let rect = Blockly.utils.dom.createSvgElement('rect',
            {
                'class': 'blocklyFlyoutButtonShadow',
                'rx': 2, 'ry': 2, 'y': 0, 'x': 1
            },
            this.fieldGroup_);

        this.createTextElement_();

        if (workspace.getRenderer().name === "thrasos") {
            this.textElement_.setAttribute("y", 8);
            this.textElement_.setAttribute("x", this.textElement_.getAttribute("x") + 4);
        } else {
            this.textElement_.setAttribute("y", 13);
            this.textElement_.setAttribute("x", this.textElement_.getAttribute("x") + 5);
        }

        if (this.class_)
            Blockly.utils.dom.addClass(this.textElement_, this.class_);

        rect.setAttribute('width', 93);
        rect.setAttribute('height', 15);
        this.rect = rect; // This is so we can update its shape

        this.lastClickTime = -1;
    };

    // Updates the shape of the field and of the rectangle surrounding the text
    entryField.updateSize_ = function () {
        this.size_.height = 14;
        if (this.textElement_)
            this.size_.width = Blockly.utils.dom.getTextWidth(this.textElement_) + 12;
        else
            this.size_.width = 93;
        this.rect.setAttribute('width', Blockly.utils.dom.getTextWidth(this.textElement_) + 8);
    };

    // Function to handle clicking
    entryField.onMouseDown_ = function (e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                javabridge.openEntrySelector(type, {
                    'callback': function (data) {
                        if (data !== undefined) {
                            entry = data;
                        } else {
                            entry = getDefaultEntry();
                        }

                        entryField.updateDisplay();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };

    // We store only the actual value in the text content, the readable name is loaded with the procedure
    entryField.toXml = function (fieldElement) {
        fieldElement.textContent = this.getValue();
        return fieldElement;
    };

    // We load the readable name again after opening the procedure, in case the entry has a new readable name
    entryField.fromXml = function (fieldElement) {
        if (fieldElement && fieldElement.textContent) {
            let readableName = javabridge.getReadableNameOf(fieldElement.textContent, type);
            if (!readableName) // The readable name is an empty string because it couldn't be found
                readableName = fieldElement.textContent; // In this case, we use the actual value
            entry = fieldElement.textContent + ',' + readableName;
        }
        else
            entry = getDefaultEntry();
        entryField.updateDisplay();
    };

    // Returns the readable text
    entryField.getText = function () {
        if (entry && entry.split(',').length === 2) {
            return entry.split(',')[1];
        }
        return javabridge.t('blockly.extension.data_list_selector.no_entry');
    }

    // Returns the actual value of the selected entry. Only this value is saved in the procedure XML
    entryField.getValue = function () {
        if (entry && entry.split(',').length === 2) {
            return entry.split(',')[0];
        }
        return '';
    }

    entryField.updateDisplay = function () {
        if (entry.split(',').length === 2) {
            this.setValue(entry.split(',')[0]);
        } else {
            this.setValue('');
        }
        this.forceRerender(); // Update the selected text and shape
    };

    return entryField;
}