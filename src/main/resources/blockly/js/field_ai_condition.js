Blockly.Extensions.register('ai_condition_selector',
    function () {
        this.appendDummyInput()
            .appendField(AIConditionFieldImpl(), 'condition');
    });

function AIConditionFieldImpl() {
    let condition = 'null,null';
    let conditionfield = new Blockly.FieldLabelSerializable('Conditions: OO', 'condition-label');
    conditionfield.EDITABLE = true;
    conditionfield.SERIALIZABLE = true;
    conditionfield.initView = function () {
        let rect = Blockly.utils.dom.createSvgElement('rect',
            {
                'class': 'blocklyFlyoutButtonShadow',
                'rx': 2, 'ry': 2, 'y': 0, 'x': 1
            },
            this.fieldGroup_);

        this.createTextElement_();
        this.textElement_.setAttribute("x", this.textElement_.getAttribute("x") + 4);
        if (this.class_)
            Blockly.utils.dom.addClass(this.textElement_, this.class_);

        rect.setAttribute('width', 93);
        rect.setAttribute('height', 15);

        this.lastClickTime = -1;
    };
    conditionfield.updateSize_ = function () {
        this.size_.height = 14;
        this.size_.width = 93;
    };
    conditionfield.onMouseDown_ = function (e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                javabridge.openAIConditionEditor(condition, {
                    'callback': function (data) {
                        if (data !== undefined) {
                            condition = data;
                        } else {
                            condition = 'null,null';
                        }

                        conditionfield.updateDisplay();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };
    conditionfield.toXml = function (fieldElement) {
        fieldElement.textContent = condition;
        return fieldElement;
    };

    conditionfield.fromXml = function (fieldElement) {
        condition = fieldElement.textContent;
        conditionfield.updateDisplay();
    };

    conditionfield.updateDisplay = function () {
        if (condition.split(',').length === 2) {
            this.setValue('Conditions: ' +
                (condition.split(',')[0] !== 'null' ? 'X' : 'O') +
                (condition.split(',')[1] !== 'null' ? 'X' : 'O')
            );
        } else {
            this.setValue('Conditions: OO');
        }
    };

    return conditionfield;
}