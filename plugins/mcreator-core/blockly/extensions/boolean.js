Blockly.Extensions.register('boolean',
    function () {
        this.appendDummyInput().appendField(javabridge.t("blockly.block.set_var"))
            .appendField(new Blockly.FieldDropdown(getVariablesOfType("Date")), 'VAR')
    });