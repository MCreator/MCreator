/**
 * This class represents a MCItem selector field
 */
class FieldMCItemSelector extends Blockly.FieldImage {

    EDITABLE = true;
    SERIALIZABLE = true;
    CURSOR = 'default';

    constructor(opt_supported_mcitems = 'all', opt_validator) {
        super('', 36, 36, '');
        this.supported_mcitems = opt_supported_mcitems // The type of selector to open ("allblocks" will open the block selector)

        if (opt_validator)
            this.setValidator(opt_validator);

        // Show the full name of the selected mcitem, or the "Double click to select value" message
        let thisField = this;
        this.setTooltip(function () {
            return thisField.getValue() || (thisField.supported_mcitems === 'allblocks' ?
                javabridge.t('blockly.field_mcitem_selector.tooltip.empty_block') :
                javabridge.t('blockly.field_mcitem_selector.tooltip.empty_block_item'));
        });
    };

    // Create the field from the json definition
    static fromJson(options) {
        return new this(Blockly.utils.parsing.replaceMessageReferences(options['supported_mcitems']), undefined);
    };

    // Initialize the field visuals
    initView() {
        this.imageElement = Blockly.utils.dom.createSvgElement(
            'image',
            {
                'height': this.imageHeight_ + 'px',
                'width': this.size_.width + 'px',
                'style': 'cursor: default;'
            },
            this.fieldGroup_);
        if (this.imageElement) {
            this.imageElement.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', this.src_ || '');
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
                        const group = Blockly.Events.getGroup();
                        Blockly.Events.setGroup(true);
                        thisField.setValue(selected || '');
                        Blockly.Events.setGroup(group);
                        javabridge.triggerEvent();
                    }
                });
                this.lastClickTime = -1;
            } else {
                this.lastClickTime = new Date().getTime();
            }
        }
    };

    // Update the value and the image of this field
    doValueUpdate_(newValue) {
        this.value_ = newValue;
        this.src_ = javabridge.getMCItemURI(newValue);
        if (this.imageElement) {
            this.imageElement.setAttributeNS('http://www.w3.org/1999/xlink', 'xlink:href', this.src_ || '');
        }
    };
}

// Register this field, so that it can be added without extensions
Blockly.fieldRegistry.register('field_mcitem_selector', FieldMCItemSelector);