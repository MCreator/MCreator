function FieldMCItem(supported_mcitems) {
    let mcitem = null;
    let mcitemfield = new Blockly.FieldImage('./res/b.png', 36, 36, '');
    mcitemfield.EDITABLE = true;
    mcitemfield.SERIALIZABLE = true;
    mcitemfield.initView = function () {
        this.imageElement_ = Blockly.utils.dom.createSvgElement(
            'image',
            {
                'height': this.imageHeight_ + 'px',
                'width': this.size_.width + 'px',
                'style': 'cursor: default;'
            },
            this.fieldGroup_);
        if (this.imageElement_) {
            this.imageElement_.setAttributeNS('http://www.w3.org/1999/xlink',
                'xlink:href', this.src_ || '');
        }
        this.sourceBlock_.getSvgRoot().appendChild(this.fieldGroup_);
        this.lastClickTime = -1;
    };
    mcitemfield.onMouseDown_ = function (e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            // double click detection, if double, open gui and dont propagate event
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                javabridge.openMCItemSelector(supported_mcitems, {
                    'callback': function (selected) {
                        mcitemfield.setValue(selected);
                        javabridge.triggerEvent();
                    }
                });
                this.lastClickTime = -1;
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };
    mcitemfield.getValue = function () {
        return mcitem;
    };
    mcitemfield.setValue = function (mcitem_new) {
        this.src_ = javabridge.getMCItemURI(mcitem_new);
        if (this.imageElement_) {
            this.imageElement_.setAttributeNS('http://www.w3.org/1999/xlink',
                'xlink:href', this.src_ || '');
        }
        mcitem = mcitem_new;
    };
    mcitemfield.setValue(null);
    return mcitemfield;
}

Blockly.Blocks['mcitem_allblocks'] = {
    init: function () {
        let block = this;
        this.appendDummyInput()
            .appendField(new FieldMCItem("allblocks"), "value")
            .appendField(new Blockly.FieldImage("./res/b.png", 8, 36));
        this.setOutput(true, 'MCItemBlock');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(60);
        this.setTooltip(function () {
            let value = block.getFieldValue('value');
            return value == null ? "Double click to select block" : value;
        });
    }
};

Blockly.Blocks['mcitem_all'] = {
    init: function () {
        let block = this;
        this.appendDummyInput()
            .appendField(new FieldMCItem("all"), "value")
            .appendField(new Blockly.FieldImage("./res/bi.png", 8, 36));
        this.setOutput(true, 'MCItem');
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setColour(350);
        this.setTooltip(function () {
            let value = block.getFieldValue('value');
            return value == null ? "Double click to select item/block" : value;
        });
    }
};