/**
 * This class represents a condition selector for AI blocks
 */
class FieldAiConditionSelector extends Blockly.Field {

    EDITABLE = true;
    SERIALIZABLE = true;
    CURSOR = 'default';

    constructor(opt_validator) {
        super('null,null', opt_validator);

        // Show the selected conditions, or the "Double click to select conditions" message
        let thisField = this;
        this.setTooltip(function () {
            let conditions = thisField.getValue() && thisField.getValue().split(',');
            if (!conditions)
                return javabridge.t('blockly.field_ai_condition_selector.tooltip.empty');

            let startCond = conditions[0];
            let continueCond = conditions[1];
            // If no condition is selected, show the "Double click to select conditions" message
            if (startCond === 'null' && continueCond === 'null')
                return javabridge.t('blockly.field_ai_condition_selector.tooltip.empty');

            let tooltip = '';
            if (startCond !== 'null') {
                tooltip += javabridge.t('blockly.field_ai_condition_selector.tooltip.start_condition') + startCond +
                    (continueCond !== 'null' ? '\n' : ''); // Add a new line if both conditions are selected
            }
            if (continueCond !== 'null') {
                tooltip += javabridge.t('blockly.field_ai_condition_selector.tooltip.continue_condition') + continueCond;
            }
            return tooltip;
        });
    };

    // Create the field from the json definition
    static fromJson(options) {
        return new this(undefined);
    };

    // Function to handle clicking
    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                let thisField = this;
                javabridge.openAIConditionEditor(this.getValue() || 'null,null', { // If somehow the value is missing, pass 'null,null'
                    'callback': function (data) {
                        const group = Blockly.Events.getGroup();
                        Blockly.Events.setGroup(true);
                        thisField.setValue(data || 'null,null');
                        Blockly.Events.setGroup(group);
                        javabridge.triggerEvent();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };

    // Get the text that is shown in the Blockly editor
    getText_() {
        let currentValues = this.getValue().split(',');
        if (currentValues.length === 2) {
            return javabridge.t('blockly.field_ai_condition_selector.conditions') +
                (currentValues[0] === 'null' ? 'O' : 'X') +
                (currentValues[1] === 'null' ? 'O' : 'X');
        }
        return javabridge.t('blockly.field_ai_condition_selector.conditions') + 'OO';
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_ai_condition_selector', FieldAiConditionSelector);