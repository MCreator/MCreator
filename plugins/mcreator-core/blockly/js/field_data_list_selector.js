/**
 * This class represents a data list field that can be double-clicked to open a list entry selector.
 * The behaviour is similar to block/item selectors or condition selectors for entity AI blocks
 */
class FieldDataListSelector extends Blockly.FieldLabelSerializable {
    constructor(datalist = '', opt_validator, opt_config) {
        super(javabridge.t('blockly.extension.data_list_selector.no_entry'), 'entry-label');
        this.type = datalist;
        this.typeFilter = null;
        this.customEntryProviders = null;
        this.entry = FieldDataListSelector.getDefaultEntry();
        this.setTooltip(this.getText_());

        this.EDITABLE = true;

        if (opt_config)
            this.configure_(opt_config);
        if (opt_validator)
            this.setValidator(opt_validator);
    }

    // The default entry is ",No entry selected". Since the value is an empty string, the procedure editor will show a compile error
    static getDefaultEntry() {
        return ',' + javabridge.t('blockly.extension.data_list_selector.no_entry');
    }

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
    }

    // Create the field from the json definition
    static fromJson(options) {
        return new this(Blockly.utils.parsing.replaceMessageReferences(options['datalist']), undefined, options);
    }

    // Initialize the field with a rectangle surrounding the text
    initView() {
        this.createBorderRect_();
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

        if (this.textElement_)
            this.borderRect_.setAttribute('width', Blockly.utils.dom.getTextWidth(this.textElement_) + 8);
        else
            this.borderRect_.setAttribute('width', 93);
        this.borderRect_.setAttribute('height', 15);

        this.lastClickTime = -1;
    };

    // Function to handle clicking
    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                let thisField = this; // reference to this field, to use in the callback function
                javabridge.openEntrySelector(this.type, this.typeFilter, this.customEntryProviders, {
                    'callback': function (data) {
                        if (data !== undefined) {
                            thisField.entry = data;
                        } else {
                            thisField.entry = FieldDataListSelector.getDefaultEntry();
                        }

                        javabridge.triggerEvent();
                        thisField.updateDisplay();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };

    // We store only the actual value in the text content, the readable name is loaded with the procedure
    toXml(fieldElement) {
        fieldElement.textContent = this.getValue();
        return fieldElement;
    };

    // We load the readable name again after opening the procedure, in case the entry has a new readable name
    fromXml(fieldElement) {
        if (fieldElement && fieldElement.textContent) {
            let readableName = javabridge.getReadableNameOf(fieldElement.textContent, this.type);
            if (!readableName) // The readable name is an empty string because it couldn't be found
                readableName = fieldElement.textContent; // In this case, we use the actual value
            this.entry = fieldElement.textContent + ',' + readableName;
        }
        else
            this.entry = FieldDataListSelector.getDefaultEntry();
        this.updateDisplay();
    };

    // Returns the readable text
    getText_() {
        if (this.entry && this.entry.split(',').length === 2) {
            return this.entry.split(',')[1];
        }
        return javabridge.t('blockly.extension.data_list_selector.no_entry');
    }

    // Returns the actual value of the selected entry. Only this value is saved in the procedure XML
    getValue() {
        if (this.entry && this.entry.split(',').length === 2) {
            return this.entry.split(',')[0];
        }
        return '';
    }

    updateDisplay() {
        if (this.entry.split(',').length === 2) {
            this.setValue(this.entry.split(',')[0]);
        } else {
            this.setValue('');
        }
        this.setTooltip(this.getText_()); // Update the field tooltip
        this.forceRerender(); // Update the selected text and shape
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_data_list_selector', FieldDataListSelector);