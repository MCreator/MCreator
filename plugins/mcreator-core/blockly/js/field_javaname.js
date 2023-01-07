FieldJavaName = function(opt_value, opt_validator, opt_config) {
    opt_value = this.doClassValidation_(opt_value);
    FieldJavaName.superClass_.constructor.call(this, opt_value, opt_validator, opt_config);
};
Blockly.utils.object.inherits(FieldJavaName, Blockly.FieldTextInput);

FieldJavaName.prototype.doClassValidation_ = function(newValue) {
    if (typeof newValue != 'string') {
        return null;
    }

    if (!newValue.match(/^([a-zA-Z_$][a-zA-Z\d_$]*)$/)) {
        return null;
    }

    return newValue;
};

FieldJavaName.fromJson = function(options) {
    return new FieldJavaName(Blockly.utils.parsing.replaceMessageReferences(options['text'], undefined, options));
};

Blockly.fieldRegistry.register('field_javaname', FieldJavaName);
