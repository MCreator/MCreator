Blockly.Extensions.register('arg_procedure',
    function () {
        this.appendDummyInput()
            .appendField(ArgProcedureFieldImpl(), 'procedure');
    });

function ArgProcedureFieldImpl() {
    let procedure = 'null';
    let procedurefield = new Blockly.FieldLabelSerializable('Procedure: none', 'procedure-label');
    procedurefield.EDITABLE = true;
    procedurefield.SERIALIZABLE = true;
    procedurefield.initView = function () {
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

        rect.setAttribute('width', 113);
        rect.setAttribute('height', 15);

        this.lastClickTime = -1;
    };
    procedurefield.updateSize_ = function () {
        this.size_.height = 14;
        this.size_.width = 113;
    };
    procedurefield.onMouseDown_ = function (e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                javabridge.openArgExecuteProcedureEditor(procedure, {
                    'callback': function (data) {
                        if (data !== undefined) {
                            procedure = data;
                        } else {
                            procedure = 'null';
                        }

                        procedurefield.updateDisplay();
                    }
                });
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };
    procedurefield.toXml = function (fieldElement) {
        fieldElement.textContent = procedure;
        return fieldElement;
    };

    procedurefield.fromXml = function (fieldElement) {
        procedure = fieldElement.textContent;
        procedurefield.updateDisplay();
    };

    procedurefield.updateDisplay = function () {
        if (procedure.split(',').length === 1) {
            this.setValue('Procedure: ' +
                (procedure.split(',')[0] !== 'null' ? 'X' : 'none')
            );
        } else {
            this.setValue('Procedure: none');
        }
    };

    return procedurefield;
}