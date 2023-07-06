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

    // Configure the field given a map of settings
    // Create the field from the json definition
    static fromJson(options) {
        return new this(Blockly.utils.parsing.replaceMessageReferences(options['datalist']), undefined, options);
    };

    // Update the value of this selector
    doValueUpdate_(newValue) {
        if (newValue !== this.value_) { // If the value is different, update the readable name
            this.readableName = javabridge.getReadableNameOf(newValue, this.type) || newValue;
        }
        super.doValueUpdate_(newValue);
    };

    // Get the text that is shown in the Blockly editor
    getText_() {
        return this.readableName;
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_data_list_dropdown', FieldDataListDropdown);