class FieldJavaName extends Blockly.FieldTextInput {

    doClassValidation_(newValue) {
        if (typeof newValue != 'string') {
            return null;
        }

        if (!newValue.match(/^([a-zA-Z_$][a-zA-Z\d_$]*)$/)) {
            return null;
        }

        return newValue;
    }

}

Blockly.fieldRegistry.register('field_javaname', FieldJavaName);
