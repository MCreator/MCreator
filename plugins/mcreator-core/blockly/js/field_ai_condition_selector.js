/**
 * This class represents a condition selector for AI blocks
 */
class FieldAiConditionSelector extends Blockly.FieldLabelSerializable {
    constructor(opt_validator) {
        super('Conditions: OO', 'condition-label');
        this.condition = 'null,null';

        this.EDITABLE = true;

        if (opt_validator)
            this.setValidator(opt_validator);
    }

    // Create the field from the json definition
    static fromJson(options) {
        return new this(undefined);
    }

    // Initialize the field with a rectangle surrounding the text
    initView() {
        let rect = Blockly.utils.dom.createSvgElement('rect',
            {
                'class': 'blocklyFlyoutButtonShadow',
                'rx': 2, 'ry': 2, 'y': 0, 'x': 1
            },
            this.fieldGroup_);
        this.createTextElement_();

        if (workspace.getRenderer().name === "thrasos") {
            this.textElement_.setAttribute("y", 8);
            this.textElement_.setAttribute("x", this.textElement_.getAttribute("x") + 3);
        } else {
            this.textElement_.setAttribute("y", 13);
            this.textElement_.setAttribute("x", this.textElement_.getAttribute("x") + 4);
        }

        if (this.class_)
            Blockly.utils.dom.addClass(this.textElement_, this.class_);

        rect.setAttribute('width', 93);
        rect.setAttribute('height', 15);
        this.lastClickTime = -1;
    }

    updateSize_() {
        this.size_.height = 14;
        this.size_.width = 93;
    }

    // Function to handle clicking
    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                let thisField = this;
                javabridge.openAIConditionEditor(this.condition, {
                    'callback': function (data) {
                        if (data) {
                            thisField.condition = data;
                        } else {
                            thisField.condition = 'null,null';
                        }

                        thisField.updateDisplay();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    }

    toXml(fieldElement) {
        fieldElement.textContent = this.condition;
        return fieldElement;
    }

    fromXml(fieldElement) {
        this.condition = fieldElement.textContent || 'null,null';
        this.updateDisplay();
    }

    updateDisplay() {
        if (this.condition.split(',').length === 2) {
            this.setValue('Conditions: ' +
                (this.condition.split(',')[0] !== 'null' ? 'X' : 'O') +
                (this.condition.split(',')[1] !== 'null' ? 'X' : 'O')
            );
        } else {
            this.setValue('Conditions: OO');
        }
    }
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_ai_condition_selector', FieldAiConditionSelector);