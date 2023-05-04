/**
 * This class represents a condition selector for AI blocks
 */
class FieldAiConditionSelector extends Blockly.Field {
    constructor(opt_validator) {
        super('null,null', opt_validator);

        this.EDITABLE = true;
        this.SERIALIZABLE = true;
        this.CURSOR = 'default';
    }

    // Create the field from the json definition
    static fromJson(options) {
        return new this(undefined);
    }

    // Function to handle clicking
    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                let thisField = this;
                javabridge.openAIConditionEditor(this.getValue() || 'null,null', { // If somehow the value is missing, pass 'null,null'
                    'callback': function (data) {
                        thisField.setValue(data || 'null,null');
                        javabridge.triggerEvent();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    }

    getText_() {
        let currentValues = this.getValue().split(',');
        if (currentValues.length === 2) {
            return 'Conditions: ' + 
                (currentValues[0] === 'null' ? 'O' : 'X') + 
                (currentValues[1] === 'null' ? 'O' : 'X');
        }
        return 'Conditions: OO';
    }
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_ai_condition_selector', FieldAiConditionSelector);