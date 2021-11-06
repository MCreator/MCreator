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
            arrayToBlocklyDropDownArray(javabridge.getListOf("planttype"))), 'planttype');
    });