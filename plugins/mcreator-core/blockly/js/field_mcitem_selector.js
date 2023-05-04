/**
 * This class represents a MCItem selector field
 */
class FieldMCItemSelector extends Blockly.FieldImage {
    constructor(opt_supported_mcitems = 'all', opt_validator) {
        super('', 36, 36, '');
        this.supported_mcitems = opt_supported_mcitems // The type of selector to open ("allblocks" will open the block selector)
        this.mcitem = null; // The selected mcitem

        this.EDITABLE = true;
        this.SERIALIZABLE = true;

        if (opt_validator)
            this.setValidator(opt_validator);
    }

    // Create the field from the json definition
    static fromJson(options) {
        return new this(Blockly.utils.parsing.replaceMessageReferences(options['supported_mcitems']), undefined);
    }

    // Initialize the field visuals
    initView() {
        this.imageElement_ = Blockly.utils.dom.createSvgElement(
            'image',
            {
                'height': this.imageHeight_ + 'px',
                'width': this.size_.width + 'px',
                'style': 'cursor: default;'
            },
            this.fieldGroup_);
        if (this.imageElement_) {
            this.imageElement_.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', this.src_ || '');
        }
        this.sourceBlock_.getSvgRoot().appendChild(this.fieldGroup_);
        this.lastClickTime = -1;
    };

    // Function to handle clicking
    onMouseDown_(e) {
        if (this.sourceBlock_ && !this.sourceBlock_.isInFlyout) {
            if (this.lastClickTime !== -1 && ((new Date().getTime() - this.lastClickTime) < 500)) {
                e.stopPropagation(); // fix so the block does not "stick" to the mouse when the field is clicked
                let thisField = this; // reference to this field, to use in the callback function
                javabridge.openMCItemSelector(this.supported_mcitems, {
                    'callback': function (selected) {
                        thisField.setValue(selected);
                        javabridge.triggerEvent();
                    }
                });
                this.lastClickTime = -1;
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };

    getValue() {
        return this.mcitem;
    };

    setValue(new_mcitem) {
        this.src_ = javabridge.getMCItemURI(new_mcitem);
        if (this.imageElement_) {
            this.imageElement_.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', this.src_ || '');
        }
        this.mcitem = new_mcitem;
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_mcitem_selector', FieldMCItemSelector);