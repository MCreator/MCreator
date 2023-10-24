/**
 * This class represents a data list field that can be double-clicked to open a list entry selector.
 * The behaviour is similar to block/item selectors or condition selectors for entity AI blocks
 */
class FieldDataListSelector extends Blockly.Field {

    EDITABLE = true;
    SERIALIZABLE = true;
    CURSOR = 'default';

    constructor(datalist = '', opt_validator, opt_config) {
        super('', opt_validator, opt_config);
        this.type = datalist;
        this.typeFilter = null;
        this.customEntryProviders = null;

        this.maxDisplayLength = 75;

        if (opt_config)
            this.configure_(opt_config);

        // Show the full name of the selected value, or the "Double click to select value" message
        let thisField = this;
        this.setTooltip(function () {
            return thisField.getValue() ?
                thisField.readableName :
                javabridge.t('blockly.field_data_list_selector.tooltip.empty');
        });
    };

    // Get the default text for when no value is selected
    static getDefaultText() {
        return javabridge.t('blockly.extension.data_list_selector.no_entry');
    };

    // Configure the field given a map of settings
    configure_(config) {
        super.configure_(config);

        // If present, set the 'typeFilter' value
        let opt_typeFilter = Blockly.utils.parsing.replaceMessageReferences(config['typeFilter']);
        if (opt_typeFilter)
            this.typeFilter = opt_typeFilter;

        // If present, set the 'customEntryProviders' value
        let opt_customEntryProviders = config['customEntryProviders'];
        if (opt_customEntryProviders)
            this.customEntryProviders = opt_customEntryProviders;
    };

    // Create the field from the json definition
    static fromJson(options) {
        return new this(Blockly.utils.parsing.replaceMessageReferences(options['datalist']), undefined, options);
    };

    // Function to handle clicking
    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                let thisField = this; // reference to this field, to use in the callback function
                let customEntryProviders = typeof this.customEntryProviders === 'function' ?
                    this.customEntryProviders() :
                    this.customEntryProviders;
                javabridge.openEntrySelector(this.type, this.typeFilter, customEntryProviders, {
                    'callback': function (value, readableName) {
                        thisField.cachedReadableName = readableName || value;
                        const group = Blockly.Events.getGroup();
                        Blockly.Events.setGroup(true);
                        thisField.setValue(value);
                        Blockly.Events.setGroup(group);
                        javabridge.triggerEvent();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };

    // Update the value of this selector
    doValueUpdate_(newValue) {
        if (newValue !== this.value_) { // If the value is different, update the readable name
            this.updateReadableName(newValue);
        }
        super.doValueUpdate_(newValue);
    };

    // Get the text that is shown in the Blockly editor
    getText_() {
        return this.readableName || FieldDataListSelector.getDefaultText();
    };

    // Update the readable name of this field
    updateReadableName(value) {
        // First check if there's a cached readable name (after selecting a value)
        if (this.cachedReadableName) {
            this.readableName = this.cachedReadableName;
            this.cachedReadableName = null;
        }
        // If there isn't (for example after opening a saved procedure), try to get the readable name from the value
        else if (value) {
            this.readableName = javabridge.getReadableNameOf(value, this.type) || value;
        } else {
            this.readableName = FieldDataListSelector.getDefaultText(); // Fallback to the "No entry selected" message
        }
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_data_list_selector', FieldDataListSelector);