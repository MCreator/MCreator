/**
 * This class represents a data list field that can be used to choose value from potentially short data lists.
 */
class FieldDataListDropdown extends Blockly.FieldDropdown {

    constructor(datalist = '', opt_validator, opt_config) {
        super(function () {
            return arrayToBlocklyDropDownArray(javabridge.getListOf(datalist));
        }, opt_validator, opt_config);
        this.type = datalist;
        this.maxDisplayLength = 75;
    };

    // Create the field from the json definition
    static fromJson(options) {
        return new this(Blockly.utils.parsing.replaceMessageReferences(options['datalist']), undefined, options);
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_data_list_dropdown', FieldDataListDropdown);