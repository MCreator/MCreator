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

                        javabridge.triggerEvent();
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