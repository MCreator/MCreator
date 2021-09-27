Blockly.Extensions.register('is_custom_loop',
    function () {
        Blockly.Constants.Loops.CONTROL_FLOW_IN_LOOP_CHECK_MIXIN.LOOP_TYPES.push(this.type);
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
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArrayWithReadableNames(javabridge.getListOf("biome"),
                javabridge.getReadableListOf("biome"))), 'biome');
    });

Blockly.Extensions.register('biome_dictionary_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("biomedictionarytypes_datalist"))), 'biomedict');
    });

Blockly.Extensions.register('entity_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArrayWithReadableNames(javabridge.getListOf("entity"),
                javabridge.getReadableListOf("entity"))), 'entity');
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

Blockly.Extensions.register('gamemode_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("gamemodes_datalist"))), 'gamemode');
    });

Blockly.Extensions.register('damagesource_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("damagesources_datalist"))), 'damagesource');
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
            .appendField(new Blockly.FieldDropdown(
                arrayToBlocklyDropDownArray(javabridge.getListOf("sound"))), 'sound');
    });

Blockly.Extensions.register('sound_category_list_provider',
    function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.extension.sound_category_list"))
            .appendField(new Blockly.FieldDropdown(
                arrayToBlocklyDropDownArray(javabridge.getListOf("soundcategories_datalist"))), 'soundcategory');
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

Blockly.Extensions.register('material_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("material"))), 'material');
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

Blockly.Extensions.register('plant_type_list_provider',
    function () {
        this.appendDummyInput().appendField(new Blockly.FieldDropdown(
            arrayToBlocklyDropDownArray(javabridge.getListOf("planttype_datalist"))), 'planttype');
    });